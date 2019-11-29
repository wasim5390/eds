package com.optimus.eds.db.dao;

import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing_models.PriceConditionDetailsWithScale;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface PriceConditionDetailsDao extends PricingDao {
    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND productDefinitionId=:productDefinitionId AND (bundleId =:bundleId OR bundleId=0)")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetails(int conditionId, int productDefinitionId, Integer bundleId);


    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND outletId=:outletId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetails(int conditionId, int outletId);

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND routeId=:routeId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetailsRoute(int conditionId, int routeId);

    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND distributionId=:distributionId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetailsDistribution(int conditionId, int distributionId);


    @Query("SELECT * FROM PriceConditionDetail Where priceConditionId=:conditionId AND productDefinitionId=:productDefinitionId")
    @Transaction
    Maybe<PriceConditionDetailsWithScale> findPriceConditionDetail(int conditionId, int productDefinitionId);



}
