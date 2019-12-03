package com.optimus.eds.ui.order.pricing;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.common.StringUtils;
import com.optimus.eds.Enums;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.PriceConditionEntitiesDao;
import com.optimus.eds.db.dao.PricingDao;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing_models.PcClassWithPcType;
import com.optimus.eds.db.entities.pricing_models.PcTypeWithPc;
import com.optimus.eds.db.entities.pricing_models.PcWithAcessSeqAndPcDetails;
import com.optimus.eds.db.entities.pricing_models.PriceConditionDetailsWithScale;
import com.optimus.eds.model.OrderResponseModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PricingManager {
    private String TAG = PricingManager.class.getSimpleName();
    private static PricingManager ourInstance=null;

    private final AppDatabase appDatabase;
    private final PricingDao pricingDao;
    private final PriceConditionEntitiesDao priceConditionEntitiesDao;

    public static PricingManager getInstance(Application application) {
        if(ourInstance==null)
            ourInstance = new PricingManager(application);
        return ourInstance;
    }

    private PricingManager(Application application) {

        appDatabase = AppDatabase.getDatabase(application);
        pricingDao = appDatabase.pricingDao();
        priceConditionEntitiesDao = appDatabase.priceConditionEntitiesDao();
    }


    public Single<List<PcClassWithPcType>> loadPricing() {

      return   pricingDao.findPriceConditionClassWithTypes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single());
    }


    public OrderResponseModel calculatePriceBreakdown(List<PcClassWithPcType> pcClassWithPcTypes,OrderResponseModel orderModel){
        if(pcClassWithPcTypes==null || pcClassWithPcTypes.isEmpty()) {
            orderModel.setSuccess(false);
            return orderModel;
        }

        Gson gson = new Gson();
        HashMap<OrderDetail,List<OrderDetail>> orderItems = ComposeNewOrderItemsListForCalc(orderModel.getOrderDetails());
        List<OrderDetail> finalOrderDetailList = new ArrayList<>();
        Double payable = 0.0;
        PriceOutputDTO priceOutputDTO=null;
        for(Map.Entry<OrderDetail,List<OrderDetail>> orderDetailHashMap:orderItems.entrySet())
        {
            OrderDetail orderDetailKey = orderDetailHashMap.getKey();
            for(OrderDetail orderDetail:orderDetailHashMap.getValue()){
                priceOutputDTO = getOrderItemPrice(pcClassWithPcTypes, null,orderDetail.getMobileOrderDetailId(),orderModel.getOutletId()
                        ,orderDetail.getProductTempDefId(),orderDetail.getProductTempQuantity()
                        ,orderModel.getRouteId(),orderModel.getDistributionId() );
                Log.wtf(TAG, "onSuccess: "+priceOutputDTO.getTotalPrice()+" PriceBreakdown: "+priceOutputDTO.getPriceBreakdown().size() );
                String gsonText = gson.toJson(priceOutputDTO.getPriceBreakdown());
                if( orderDetail.getProductTempDefId()== orderDetail.getCartonDefinitionId()) {
                    List<CartonPriceBreakDown> priceBreakDown =  gson.fromJson(gsonText, new TypeToken<List<CartonPriceBreakDown>>(){}.getType());
                    orderDetailKey.setCartonPriceBreakDown(priceBreakDown);
                    orderDetailKey.setCartonQuantity(orderDetail.getProductTempQuantity());
                    orderDetailKey.setCartonTotalPrice(priceOutputDTO.getTotalPrice().doubleValue());

                }else{
                    orderDetailKey.setUnitPriceBreakDown(new ArrayList<>(priceOutputDTO.getPriceBreakdown()));
                    orderDetailKey.setUnitQuantity(orderDetail.getProductTempQuantity());
                    orderDetailKey.setUnitTotalPrice(priceOutputDTO.getTotalPrice().doubleValue());
                }
                orderDetailKey.setCartonOrderDetailId(orderDetail.getCartonOrderDetailId());
                orderDetailKey.setUnitOrderDetailId(orderDetail.getUnitOrderDetailId());

                orderDetailKey.setLocalOrderId(orderDetail.getLocalOrderId());

                payable+=priceOutputDTO.getTotalPrice().doubleValue();
            }

            finalOrderDetailList.add(orderDetailKey);
            orderModel.setPayable(DecimalFormatter.round(payable,0));
            orderModel.setSubtotal(DecimalFormatter.round(payable,2));
        }
        orderModel.setOrderDetails(finalOrderDetailList);

        orderModel.setSuccess(true);
        return orderModel;
    }

    public HashMap<OrderDetail,List<OrderDetail>> ComposeNewOrderItemsListForCalc(List<OrderDetail> originalDetailsList){
        HashMap<OrderDetail,List<OrderDetail>> orderDetailListHashMap = new HashMap<>();

        for(OrderDetail orderDetail:originalDetailsList) {
            List<OrderDetail> newOrderLineItems = new ArrayList<>();
            try {

                if (orderDetail.getUnitQuantity()!=null &&orderDetail.getUnitQuantity() > 0) {
                    OrderDetail newUnitProd =(OrderDetail) orderDetail.clone();
                    newUnitProd.setProductTempDefId(orderDetail.getUnitDefinitionId());
                    newUnitProd.setProductTempQuantity(orderDetail.getUnitQuantity());
                    newOrderLineItems.add(newUnitProd);
                }
                if (orderDetail.getCartonQuantity()!=null && orderDetail.getCartonQuantity() > 0) {
                    OrderDetail newCartonProd = (OrderDetail) orderDetail.clone();
                    newCartonProd.setProductTempDefId(orderDetail.getCartonDefinitionId());
                    newCartonProd.setProductTempQuantity(orderDetail.getCartonQuantity());
                    newOrderLineItems.add(newCartonProd);
                }
            }catch (CloneNotSupportedException e){
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
                return orderDetailListHashMap;
            }
            orderDetailListHashMap.put(orderDetail,newOrderLineItems);
        }
        return orderDetailListHashMap;


    }


    private PromoLimitDTO GetPriceAgainstPriceCondition(int priceConditionId, String accessSequence, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, int routeId, Integer distributionId, Integer bundleId,IPromoLimitDTO iPromoLimitDTO)
    {
        PromoLimitDTO promoLimitDTO = new PromoLimitDTO();
        if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString()))
        {
            promoLimitDTO= GetPriceAgainstOutletProduct(iPromoLimitDTO,priceConditionId, outletId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        }
        else if (accessSequence.equalsIgnoreCase( Enums.AccessSequenceCode.PRODUCT.toString()))
        {
            promoLimitDTO= GetPriceAgainstProduct(iPromoLimitDTO,priceConditionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        }
        else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString()) || accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.REGION_PRODUCT.toString()))
        {
            promoLimitDTO = this.GetPriceAgainstRouteProduct(priceConditionId, routeId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        }
        else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString()))
        {
            promoLimitDTO = this.GetPriceAgainstDistributionProduct(priceConditionId, distributionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        }

        else if (accessSequence.equalsIgnoreCase( Enums.AccessSequenceCode.OUTLET.toString()))
        {
            promoLimitDTO = this.GetPriceAgainstOutlet(priceConditionId, outletId, quantity, totalPrice, scaleBasisId);
        }
        else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE.toString()))
        {
            promoLimitDTO = this.GetPriceAgainstRoute(priceConditionId, routeId, quantity, totalPrice, scaleBasisId);
        }
        else if (accessSequence.equalsIgnoreCase( Enums.AccessSequenceCode.DISTRIBUTION.toString()))
        {
            promoLimitDTO = this.GetPriceAgainstDistribution(priceConditionId, distributionId, quantity, totalPrice, scaleBasisId);
        }

        return promoLimitDTO;
    }
    private PromoLimitDTO GetPriceAgainstProduct(IPromoLimitDTO iPromoLimitDTO, int priceConditionId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId)
    {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
        PriceConditionDetailsWithScale priceConditionDetailsWithScale =priceConditionEntitiesDao.findPriceConditionDetail(priceConditionId,productDefinitionId)
                .subscribeOn(Schedulers.io()).blockingGet();
        if(priceConditionDetailsWithScale==null)
            return null;
        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null)
        {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());

            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstOutletProduct(IPromoLimitDTO iPromoLimitDTO,int priceConditionId, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId)
    {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
        PriceConditionDetailsWithScale priceConditionDetailsWithScale =  priceConditionEntitiesDao.findPriceConditionEntityOutlet(priceConditionId,outletId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities,priceConditionId,productDefinitionId,bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();
        if(priceConditionDetailsWithScale==null)
            return null;
        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null)
        {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstRouteProduct(int priceConditionId, int routeId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId)
    {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail =priceConditionEntitiesDao.findPriceConditionEntityRoute(priceConditionId,routeId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities,priceConditionId,productDefinitionId,bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null)
        {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }


        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstDistributionProduct(int priceConditionId, Integer distributionId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId)
    {


        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail =priceConditionEntitiesDao.findPriceConditionEntityDistribution(priceConditionId,distributionId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities,priceConditionId,productDefinitionId,bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetail != null)
        {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstOutlet(int priceConditionId, int outletId, int quantity, BigDecimal totalPrice, int scaleBasisId)
    {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionDetails(priceConditionId,outletId)
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetail != null)
        {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstRoute(int priceConditionId, int routeId, int quantity, BigDecimal totalPrice, int scaleBasisId)
    { PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail= priceConditionEntitiesDao.findPriceConditionDetailsRoute(priceConditionId,routeId)
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null)
        {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstDistribution(int priceConditionId, Integer distributionId, int quantity, BigDecimal totalPrice, int scaleBasisId)
    {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionDetailsDistribution(priceConditionId,distributionId)
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null)
        {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
            {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            }
            else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
            {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(),detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }


    private Maybe<PriceConditionDetailsWithScale> getPriceConditionDetailObservable(PriceConditionEntities priceConditionEntity
            , int priceConditionId, int productDefinitionId , Integer bundleId) {
        if(priceConditionEntity==null)
            return null;
        return priceConditionEntitiesDao.findPriceConditionDetail(priceConditionId,
                productDefinitionId )
                .subscribeOn(Schedulers.single());
    }


    private BigDecimal GetScaledAmount(List<PriceConditionScale> scaleList,int priceConditionDetailId, int scaleBasisId, int quantity, BigDecimal totalPrice)
    {
        BigDecimal returnAmount = BigDecimal.ZERO;
        if (scaleBasisId == Enums.ScaleBasis.Quantity || scaleBasisId == Enums.ScaleBasis.Total_Quantity)
        {
            Collections.sort(scaleList, (o1, o2) -> o1.getFrom().compareTo(o2.getFrom()));
            for(PriceConditionScale conditionScale:scaleList){

                if(conditionScale.getPriceConditionDetailId()==priceConditionDetailId && conditionScale.getFrom()<=quantity){
                    returnAmount = conditionScale.getAmount();
                    break;
                }
            }

        }
        else if (scaleBasisId == Enums.ScaleBasis.Value)
        {
            Collections.sort(scaleList, (o1, o2) -> o1.getAmount().compareTo(o2.getAmount()));
            for(PriceConditionScale conditionScale:scaleList){
                if(conditionScale.getPriceConditionDetailId()==priceConditionDetailId && conditionScale.getAmount().doubleValue()<=totalPrice.doubleValue()){
                    returnAmount = conditionScale.getAmount();
                    break;
                }
            }
        }
        return returnAmount;
    }



    PriceOutputDTO objPriceOutputDTO = new PriceOutputDTO();
    BigDecimal totalPrice = BigDecimal.ZERO; //input price for every condition class
    BigDecimal subTotal = BigDecimal.ZERO; //input price for every condition class

    boolean isPriceFound = false;

    public PriceOutputDTO getOrderItemPrice(List<PcClassWithPcType> conditionClasses,List<ProductQuantityDTO> productListDTO,
                                           int mobileOrderDetailId,
                                            int outletId, int productDefinitionId, int quantity, int routeId, Integer distributionId)
    {
        objPriceOutputDTO = new PriceOutputDTO();
        totalPrice = BigDecimal.ZERO; //input price for every condition class
        subTotal = BigDecimal.ZERO;
        isPriceFound = false;

        for (PcClassWithPcType conditionClass : conditionClasses)
        {
            PriceConditionClass priceConditionClass = conditionClass.getPriceConditionClass();
            isPriceFound = false;
            List<PcTypeWithPc> conditionTypes = conditionClass.getPcTypeWithPcList();
            for (PcTypeWithPc conditionType :conditionTypes)
            {

                PriceConditionType priceConditionType = conditionType.getPriceConditionType();
                List<PcWithAcessSeqAndPcDetails> priceConditions = conditionType.getPriceConditionList();
                Collections.sort(priceConditions,(o1, o2) -> o1.getAccessSequence().getOrder().compareTo(o2.getAccessSequence().getOrder()));
                for (PcWithAcessSeqAndPcDetails prAccSeqDetail:priceConditions) {

                    PriceCondition priceCondition = prAccSeqDetail.getPriceCondition();
                    PriceAccessSequence accessSequence = prAccSeqDetail.getAccessSequence();

                    PromoLimitDTO limitDTO = GetPriceAgainstPriceCondition( priceCondition.getPriceConditionId(), accessSequence.getSequenceCode(),
                            outletId, productDefinitionId, quantity, totalPrice, priceConditionType.getPriceScaleBasisId(), routeId,
                            distributionId, null, maxLimitDTO -> { });


                    if (limitDTO != null && limitDTO.getUnitPrice().doubleValue() > -1) {
                        isPriceFound = true;
                        UnitPriceBreakDown objSingleBlock = new UnitPriceBreakDown();

                        objSingleBlock.setPriceConditionDetailId(limitDTO.getPriceConditionDetailId());
                        objSingleBlock.setOrderDetailId(mobileOrderDetailId);
                        if (limitDTO.getLimitBy() != null) {
                            objSingleBlock.setMaximumLimit(limitDTO.getMaximumLimit().doubleValue());
                            objSingleBlock.setLimitBy(limitDTO.getLimitBy());

                        /*   var objAlreadyAvailed = _outletAvailedPromotionDataHandler.GetAlreadyAvailedValue(objSingleBlock.getPriceConditionDetailId());
                            if (objAlreadyAvailed != null)
                            {
                                if (objSingleBlock.getLimitBy() == Enums.LimitBy.Amount)
                                {
                                    objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.Amount);
                                }
                                else
                                {
                                    objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.Quantity);
                                }
                            }*/ }

                        ItemAmountDTO blockPrice = CalculateBlockPrice(totalPrice.doubleValue(), limitDTO.getUnitPrice().doubleValue(), quantity, priceConditionType.getPriceScaleBasisId(), priceConditionType.getOperationType(), priceConditionType.getCalculationType(), priceConditionType.getRoundingRule(), objSingleBlock.getLimitBy(), objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed());
                        subTotal = subTotal.add(BigDecimal.valueOf(blockPrice.getBlockPrice()));
                        totalPrice = BigDecimal.valueOf(blockPrice.getTotalPrice());
                        objSingleBlock.mPriceConditionType = priceConditionType.getName();
                        objSingleBlock.mPriceConditionClass =priceConditionClass.getName() ;
                        objSingleBlock.mPriceConditionClassOrder=priceConditionClass.getOrder();
                        objSingleBlock.mPriceCondition = priceCondition.getName();
                        objSingleBlock.mAccessSequence = accessSequence.getSequenceName();
                        objSingleBlock.mCalculationType = priceConditionType.getCalculationType();
                        objSingleBlock.mUnitPrice = limitDTO.getUnitPrice().floatValue();
                        objSingleBlock.mBlockPrice = blockPrice.getBlockPrice();
                        objSingleBlock.mTotalPrice = totalPrice.doubleValue();

                        objSingleBlock.mPriceConditionId = priceCondition.getPriceConditionId();
                        objSingleBlock.mProductDefinitionId = productDefinitionId;


                        objPriceOutputDTO.getPriceBreakdown().add(objSingleBlock);
                        objPriceOutputDTO.setTotalPrice( totalPrice);

                        if (blockPrice.IsMaxLimitReached)
                        {
                            Message message = new Message();
                            {
                                message.setMessageSeverityLevel(priceConditionClass.getSeverityLevel());
                                message.setMessageText("Max limit crossed for " + objSingleBlock.getPriceCondition());
                            }
                            objPriceOutputDTO.getMessages().add(message);
                        }

                        break;
                    }

                }


            }
       /*     if (!isPriceFound && !TextUtils.isEmpty(priceConditionClass.getSeverityLevelMessage())
                    && priceConditionClass.getSeverityLevel() != (int)enumMessageSeverityLevel.MESSAGE)
            {
                Message message = new Message();
                message.MessageSeverityLevel = priceConditionClass.getSeverityLevel();
                message.MessageText = priceConditionClass.getSeverityLevelMessage();
                objPriceOutputDTO.Messages.Add(message);
            }*/



        }
        objPriceOutputDTO.setTotalPrice(totalPrice);
        return objPriceOutputDTO;
    }








//    region "Total Price Calculation"

    private ItemAmountDTO CalculateBlockPrice(Double inputAmount, Double amount, int quantity, int scaleBasisId, int operationType, int calculationType, int roundingRule, Integer limitBy, Double maxLimit, Double alreadyAvailed)
    {


        ItemAmountDTO objReturnPrice = new ItemAmountDTO();
        double blockPrice = 0;
        int actualQuantity = quantity;
        //decimal actualAmount = amount;
        if(limitBy==null){
            limitBy = 0;
        }

        if (limitBy == Enums.LimitBy.Quantity)
        {
            if (alreadyAvailed == null)
            {
                alreadyAvailed = 0.0;
            }
            int remainingQuantity =maxLimit.intValue() - alreadyAvailed.intValue();
            if (remainingQuantity < actualQuantity)
            {
                actualQuantity = remainingQuantity;
                objReturnPrice.IsMaxLimitReached = true;
            }
        }


        if (scaleBasisId == (int)Enums.ScaleBasis.Quantity)
        {
            objReturnPrice.TotalPrice = 0;
            objReturnPrice.BlockPrice = amount * actualQuantity;
            blockPrice = amount * actualQuantity;
        }
        else if (scaleBasisId == (int)Enums.ScaleBasis.Value || scaleBasisId == (int)Enums.ScaleBasis.Total_Quantity)
        {
            objReturnPrice.TotalPrice = 0;
            objReturnPrice.BlockPrice = amount;
            blockPrice = amount;
        }




        if (calculationType == Enums.CalculationType.Fix)
        {
            if (limitBy == Enums.LimitBy.Amount)
            {
                blockPrice = getRemainingBlockPrice(amount, maxLimit, alreadyAvailed);
                if (blockPrice < amount)
                {
                    objReturnPrice.IsMaxLimitReached = true;
                }
            }
            if (operationType == Enums.OperationType.Plus)
            {
                objReturnPrice.TotalPrice = inputAmount + blockPrice;
            }
            else if (operationType == Enums.OperationType.Minus)
            {
                objReturnPrice.TotalPrice = inputAmount - blockPrice;
            }
        }
        else if (calculationType == Enums.CalculationType.Percentage)
        {
            double value = (inputAmount * amount) / 100; //If percentage, use the same amount instead of amount * quantity
            double actualValue = value;
            if (limitBy == (int)Enums.LimitBy.Amount)
            {
                actualValue = getRemainingBlockPrice(value, maxLimit, alreadyAvailed);
                if (actualValue < value)
                {
                    objReturnPrice.IsMaxLimitReached = true;
                }
            }
            objReturnPrice.BlockPrice = actualValue;
            if (operationType == (int)Enums.OperationType.Plus)
            {
                objReturnPrice.TotalPrice = inputAmount + actualValue;
            }
            else if (operationType == Enums.OperationType.Minus)
            {
                objReturnPrice.TotalPrice = inputAmount - actualValue;
            }
        }

        if (roundingRule == Enums.RoundingRule.Zero_Decimal_Precision)
        {
            objReturnPrice.TotalPrice = (int)DecimalFormatter.round(objReturnPrice.TotalPrice, 0);
        }
        else if (roundingRule == Enums.RoundingRule.Two_Decimal_Precision)
        {

            objReturnPrice.TotalPrice = DecimalFormatter.round(objReturnPrice.TotalPrice, 2);
        }
        else if (roundingRule == (int)Enums.RoundingRule.Ceiling)
        {
            objReturnPrice.TotalPrice = (int)Math.ceil(objReturnPrice.TotalPrice);
        }
        else if (roundingRule == Enums.RoundingRule.Floor)
        {
            objReturnPrice.TotalPrice = (int)Math.floor(objReturnPrice.TotalPrice);
        }

        return objReturnPrice;
    }

    private double getRemainingBlockPrice(double amount, double maxLimit, Double alreadyAvailed)
    {
        if (alreadyAvailed == null)
        {
            alreadyAvailed = 0.0;
        }
        double remainingAmount =  maxLimit -  alreadyAvailed;
        if (remainingAmount < amount)
        {
            amount = remainingAmount;
        }
        return amount;
    }

    interface IPromoLimitDTO{
        void onPriceFound(PromoLimitDTO limitDTO);
    }

}
