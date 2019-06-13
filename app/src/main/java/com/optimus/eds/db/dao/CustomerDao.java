package com.optimus.eds.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.optimus.eds.db.entities.CustomerInput;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM CustomerInput WHERE orderId=:orderId")
    Maybe<CustomerInput> findCustomerInput(Long orderId);

    @Insert(onConflict = REPLACE)
    void insertCustomerInput(CustomerInput customerInput);

    @Update
    int updateCustomerInput(CustomerInput customerInput);

    @Query("Delete FROM CustomerInput WHERE outletId=:outletId")
    void deleteCustomerInput(Long outletId);

    @Query("DELETE FROM CustomerInput")
    void deleteAllCustomerInput();
}
