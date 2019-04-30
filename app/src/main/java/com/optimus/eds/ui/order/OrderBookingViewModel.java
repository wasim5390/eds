package com.optimus.eds.ui.order;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.OrderDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.Order;
import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.model.PackageModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.PATCH;


public class OrderBookingViewModel extends AndroidViewModel {

    private ProductsDao productsDao;
    private OrderBookingRepository repository;
    private MutableLiveData<List<PackageModel>> mutablePkgList;
    private MutableLiveData<Boolean> isSaving;
    private List<Package> packages;
    private List<Product> products;
    private CompositeDisposable compositeDisposable;

    public OrderBookingViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        mutablePkgList = new MutableLiveData<>();
        isSaving = new MutableLiveData<>();
        packages = new ArrayList<>();
        productsDao = AppDatabase.getDatabase(application).productsDao();
        repository = new OrderBookingRepository(application);
        onScreenCreated();
    }

    private void onScreenCreated(){
        productsDao.findAllPackages().observeForever(packages1 -> {
            onPackagesLoaded(packages1);
        });
        /*productsDao.findAllPackages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((packages1, throwable) -> onPackagesLoaded(packages1));*/



    }

    private void onPackagesLoaded(List<Package> packages) {
        this.packages = packages;
        productsDao.findAllProduct().observeForever(products1 -> {
            onProductsLoaded(products1,this.packages);
        });
/*        productsDao.findAllProduct().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((products1, throwable) -> onProductsLoaded(products1,this.packages));*/

    }

    private void onProductsLoaded(List<Product> products,List<Package> packages){
        this.products = products;
        mutablePkgList.setValue(packageModel(packages,products));
    }



    private void onError(Throwable throwable) {

    }



    private void onSuccess(List<PackageModel> packageModels) {
        mutablePkgList.setValue(packageModels);
    }

    private List<PackageModel>  packageModel(List<Package> packages, List<Product> _products) {
        List<PackageModel> packageModels = new ArrayList<>(packages.size());

        for(Package _package: packages)
        {
            List<Product> products = getProductsById(_package.getPackageId(),_products);
            PackageModel model = new PackageModel(_package.getPackageId(),_package.getPackageName(),products);
            packageModels.add(model);
        }
        return packageModels;

    }

    private List<Product> getProductsById(Long packageId,List<Product> products){
        List<Product> filteredList = new ArrayList<>();
        for(Product product:products){
            if(product.getPkgId()==packageId)
                filteredList.add(product);
        }

        return filteredList;
    }


    public LiveData<List<PackageModel>> getProductList() {
        return mutablePkgList;
    }



    public void addOrder(Order order){
        repository.addOrder(order).observeForever(aBoolean -> {
            isSaving.setValue(aBoolean);
        });
    }

    public LiveData<Boolean> isSaving() {
        return isSaving;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
