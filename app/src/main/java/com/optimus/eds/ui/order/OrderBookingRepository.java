package com.optimus.eds.ui.order;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.OrderDetail;

import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.ProductGroup;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.model.OrderModel;
import com.optimus.eds.model.PackageModel;
import com.optimus.eds.model.PackageProductResponseModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.Response;

public class OrderBookingRepository {
    private final String TAG=OrderBookingRepository.class.getSimpleName();
    private static OrderBookingRepository repository;
    private OrderDao orderDao;
    private ProductsDao productsDao;
    private MutableLiveData<List<ProductGroup>> allGroups;
    public AppDatabase appDatabase;

    public static OrderBookingRepository singleInstance(Application application){
        if(repository==null)
            repository = new OrderBookingRepository(application);
        return repository;
    }

    public OrderBookingRepository(Application application) {

         appDatabase = AppDatabase.getDatabase(application);
        productsDao = appDatabase.productsDao();
        orderDao = appDatabase.orderDao();
        allGroups = new MutableLiveData<>();
    }


    public void createOrder(Order order){
            orderDao.insertOrder(order);
    }

    public Completable addOrderItems(List<OrderDetail> orderDetail){
       return Completable.fromAction(()->orderDao.insertOrderItems(orderDetail));
    }

    public void addOrderCartonPriceBreakDown(List<CartonPriceBreakDown> cartonPriceBreakDowns){
       orderDao.insertCartonPriceBreakDown(cartonPriceBreakDowns);
    }

    public void addOrderUnitPriceBreakDown(List<UnitPriceBreakDown> unitPriceBreakDowns){
      orderDao.insertUnitPriceBreakDown(unitPriceBreakDowns);
    }
    public Completable updateOrderItems(List<OrderDetail> orderDetails){
        return Completable.fromAction(()->orderDao.updateOrderItems(orderDetails));
    }

    public void deleteOrderItemsByGroup(Long orderId,Long groupId){
        orderDao.deleteOrderItemsByGroup(orderId,groupId);
    }


    public Completable deleteOrderItems(Long orderId){
       return Completable.fromAction(()->orderDao.deleteOrderItems(orderId));
    }

    public void deletePreviousItems(Long orderId){
        orderDao.deleteOrderItems(orderId);
    }


    public Maybe<OrderModel> findOrder(Long outletId){
        return orderDao.getOrderWithItems(outletId);
    }

    public Maybe<Order> findOrderById(Long mobileOrderId){
        return orderDao.findOrderById(mobileOrderId);
    }

    public Flowable<List<OrderModel>> findPendingOrder(){
        return orderDao.getPendingOrders(2);
    }

    public Single<List<OrderModel>> findPendingOrders(){
       return orderDao.getPendingOrdersToUpload(2);
    }

    public Completable updateOrder(Order order){
        return Completable.fromAction(()->orderDao.updateOrder(order));
    }



    protected MutableLiveData<List<ProductGroup>> findAllGroups(){
        AsyncTask.execute(() -> allGroups.postValue(productsDao.findAllProductGroups()));
        return allGroups;
    }


    protected LiveData<List<Package>> findAllPackages(){
        MutableLiveData<List<Package>> packages = new MutableLiveData<>();
        AsyncTask.execute(() -> {
           List<Package> packageList= productsDao.findAllPackages();
           packages.postValue(packageList);
        });
       return packages;
    }


    protected Single<List<Product>> findAllProductsByGroup(Long groupId){
        return productsDao.findAllProductsByGroupId(groupId);

    }

    public Single<Product> findProductById(Long productId){
        return productsDao.findProductById(productId);
    }

    public Completable updateProduct(Product product){
        return Completable.fromAction(()->productsDao.updateProduct(product));
    }

    protected List<PackageModel> packageModel(List<Package> packages, List<Product> _products) {
        List<PackageModel> packageModels = new ArrayList<>(packages.size());

        for(Package _package: packages)
        {
            List<Product> products = getProductsByPkgId(_package.getPackageId(),_products);
            if(!products.isEmpty()) {
                PackageModel model = new PackageModel(_package.getPackageId(), _package.getPackageName(), products);
                packageModels.add(model);
            }
        }
        return packageModels;

    }

    protected List<Product> getProductsByPkgId(Long packageId,List<Product> products){
        List<Product> filteredList = new ArrayList<>();
        for(Product product:products){
            if(product.getPkgId()==packageId)
                filteredList.add(product);
        }

        return filteredList;
    }

    protected Single<List<OrderDetail>> getOrderItems(Long orderId){
        return orderDao.findOrderItemsByOrderId(orderId);
    }


}
