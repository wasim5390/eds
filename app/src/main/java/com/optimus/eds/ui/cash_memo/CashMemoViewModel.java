package com.optimus.eds.ui.cash_memo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class CashMemoViewModel extends AndroidViewModel {

    public MutableLiveData<List<Product>> cartProducts;
    ProductsDao dao;
    private AppDatabase database;

    public CashMemoViewModel(@NonNull Application application) {
        super(application);
        cartProducts = new MutableLiveData<>();
        database = AppDatabase.getDatabase(application);
        dao = database.productsDao();
        getProducts(1L);
    }

    void getProducts(Long outletId){
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L,100L,"SSRB MIRINDA",10.0));
        products.add(new Product(2L,101L,"SSRB PEPSI",8.0));
        products.add(new Product(3L,103L,"SSRB SPRITE",4.0));
        cartProducts.postValue(products);

    }

    LiveData<List<Product>> getCartProducts(){
        return cartProducts;
    }
}
