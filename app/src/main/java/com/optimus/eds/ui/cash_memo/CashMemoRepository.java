package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.dao.RouteDao;
import com.optimus.eds.model.OrderModel;

import io.reactivex.Single;

public class CashMemoRepository {
    private ProductsDao productDao;
    private OrderDao orderDao;
    private RouteDao routeDao;



    public LiveData<Boolean> isSaving() {
        return isSaving;
    }

    private MutableLiveData<Boolean> isSaving;

    public CashMemoRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        productDao = appDatabase.productsDao();
        routeDao= appDatabase.routeDao();
        orderDao = appDatabase.orderDao();
        isSaving = new MutableLiveData<>();

    }

    protected Single<OrderModel> findOrder(Long outletId){
        return orderDao.getOrderWithItems(outletId);
    }
}
