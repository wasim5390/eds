
package com.optimus.eds.db.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Entity
public class Asset implements Serializable {


    @SerializedName("outletId")
    private Long outletId;
    @SerializedName("assetId")
    @PrimaryKey
    private Long mAssetId;
    @SerializedName("assetModel")
    private String mAssetModel;
    @SerializedName("assetModelId")
    private Integer mAssetModelId;
    @SerializedName("assetName")
    private String mAssetName;
    @SerializedName("assetNumber")
    private String mAssetNumber;
    @SerializedName("assetType")
    private String mAssetType;
    @SerializedName("assetTypeId")
    private Integer mAssetTypeId;
    @SerializedName("assignedDate")
    private Long mAssignedDate;
    @SerializedName("assignmentCode")
    private String mAssignmentCode;
    @SerializedName("cost")
    private Double mCost;
    @SerializedName("deposit")
    private Double mDeposit;
    @SerializedName("documentNumber")
    private String mDocumentNumber;
    @SerializedName("expiryDate")
    private Long mExpiryDate;
    @SerializedName("returnDate")
    private Long mReturnDate;
    @SerializedName("serialNumber")
    private String mSerialNumber;
    @SerializedName("TransactionType")
    private String mTransactionType;

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }
    public Long getAssetId() {
        return mAssetId;
    }


    public Integer getAssetTypeId() {
        return mAssetTypeId;
    }
    public Integer getAssetModelId() {
        return mAssetModelId;
    }

    public void setAssetModelId(Integer mAssetModelId) {
        this.mAssetModelId = mAssetModelId;
    }
    public void setAssetTypeId(Integer mAssetTypeId) {
        this.mAssetTypeId = mAssetTypeId;
    }

    public String getAssetModel() {
        return mAssetModel;
    }

    public String getAssetName() {
        return mAssetName;
    }

    public String getAssetNumber() {
        return mAssetNumber;
    }

    public String getAssetType() {
        return mAssetType;
    }

    public Long getAssignedDate() {
        return mAssignedDate;
    }

    public String getAssignmentCode() {
        return mAssignmentCode;
    }

    public Double getCost() {
        return mCost;
    }

    public Double getDeposit() {
        return mDeposit;
    }

    public String getDocumentNumber() {
        return mDocumentNumber;
    }

    public Long getExpiryDate() {
        return mExpiryDate;
    }

    public Long getReturnDate() {
        return mReturnDate;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public String getTransactionType() {
        return mTransactionType;
    }


}
