package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceBundle;
import com.optimus.eds.db.entities.pricing.PriceCondition;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing_models.PcClassWithPcType;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.DELETE;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PricingDao {
    @Query("SELECT * FROM PriceConditionClass Where PriceConditionClass.pricingLevelId=1 Order By `order`")
    @Transaction
    Single<List<PcClassWithPcType>> findPriceConditionClassWithTypes();

    @Insert(onConflict = REPLACE)
    void insertPriceConditionClasses(List<PriceConditionClass> priceConditionClasses);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionType(List<PriceConditionType> priceConditionTypes);

    @Insert(onConflict = REPLACE)
    void insertPriceCondition(List<PriceCondition> priceConditionType);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionDetail(List<PriceConditionDetail> priceConditionDetails);

    @Insert(onConflict = REPLACE)
    void insertPriceBundles(List<PriceBundle> priceAccessSequences);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionEntities(List<PriceConditionEntities> priceConditionEntities);

    @Insert(onConflict = REPLACE)
    void insertPriceAccessSequence(List<PriceAccessSequence> priceAccessSequences);

    @Insert(onConflict = REPLACE)
    void insertPriceConditionScales(List<PriceConditionScale> scales);

    @Query("DELETE FROM PriceConditionClass")
    void deleteAllPriceConditionClasses();
}
