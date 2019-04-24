package com.optimus.eds.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.Package;
import com.optimus.eds.db.entities.Product;

import java.util.List;

import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductsDao {

    @Query("SELECT * FROM Package ORDER BY packageName ASC")
    LiveData<List<Package>> findAllPackages();

    //@Query("SELECT * FROM Package ORDER BY packageName ASC")
    //List<Package> findAllPackages();

    @Query("SELECT * FROM Product ORDER BY name ASC")
    LiveData<List<Product>> findAllProduct();

    @Query("SELECT * FROM Product WHERE pkgId=:pkgId")
    LiveData<List<Product>> findAllProductsByPkg(Long pkgId);

    @Query("SELECT * FROM Product WHERE id=:id")
    LiveData<Product> findProductById(Long id);


    @Insert(onConflict = REPLACE)
    void insertProduct(Product product);

    @Insert(onConflict = REPLACE)
    void insertProducts(List<Product> products);

    @Insert(onConflict = REPLACE)
    void insertPackages(List<Package> packages);

    @Update
    int updateProduct(Product product);


    @Update
    void updateProducts(List<Product> products);

    @Update
    void updatePackage(Package pkg);

    @Update
    void updatePackages(List<Package> packages);

    @Delete
    void deleteRoute(Product product);

    @Query("DELETE FROM Product")
    void deleteAll();

    @Query("DELETE FROM Package")
    void deleteAllFromPackage();
}
