
package com.optimus.eds.ui.route;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Asset implements Serializable {

    @SerializedName("AssetId")
    private Long mAssetId;
    @SerializedName("AssetModel")
    private String mAssetModel;
    @SerializedName("AssetName")
    private String mAssetName;
    @SerializedName("AssetNumber")
    private String mAssetNumber;
    @SerializedName("AssetType")
    private String mAssetType;
    @SerializedName("AssignedDate")
    private Long mAssignedDate;
    @SerializedName("AssignmentCode")
    private String mAssignmentCode;
    @SerializedName("Cost")
    private Double mCost;
    @SerializedName("Deposit")
    private Double mDeposit;
    @SerializedName("DocumentNumber")
    private String mDocumentNumber;
    @SerializedName("ExpiryDate")
    private Long mExpiryDate;
    @SerializedName("ReturnDate")
    private Long mReturnDate;
    @SerializedName("SerialNumber")
    private String mSerialNumber;
    @SerializedName("TransactionType")
    private String mTransactionType;

    public Long getAssetId() {
        return mAssetId;
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

    public static class Builder {

        private Long mAssetId;
        private String mAssetModel;
        private String mAssetName;
        private String mAssetNumber;
        private String mAssetType;
        private Long mAssignedDate;
        private String mAssignmentCode;
        private Double mCost;
        private Double mDeposit;
        private String mDocumentNumber;
        private Long mExpiryDate;
        private Long mReturnDate;
        private String mSerialNumber;
        private String mTransactionType;

        public Asset.Builder withAssetId(Long assetId) {
            mAssetId = assetId;
            return this;
        }

        public Asset.Builder withAssetModel(String assetModel) {
            mAssetModel = assetModel;
            return this;
        }

        public Asset.Builder withAssetName(String assetName) {
            mAssetName = assetName;
            return this;
        }

        public Asset.Builder withAssetNumber(String assetNumber) {
            mAssetNumber = assetNumber;
            return this;
        }

        public Asset.Builder withAssetType(String assetType) {
            mAssetType = assetType;
            return this;
        }

        public Asset.Builder withAssignedDate(Long assignedDate) {
            mAssignedDate = assignedDate;
            return this;
        }

        public Asset.Builder withAssignmentCode(String assignmentCode) {
            mAssignmentCode = assignmentCode;
            return this;
        }

        public Asset.Builder withCost(Double cost) {
            mCost = cost;
            return this;
        }

        public Asset.Builder withDeposit(Double deposit) {
            mDeposit = deposit;
            return this;
        }

        public Asset.Builder withDocumentNumber(String documentNumber) {
            mDocumentNumber = documentNumber;
            return this;
        }

        public Asset.Builder withExpiryDate(Long expiryDate) {
            mExpiryDate = expiryDate;
            return this;
        }

        public Asset.Builder withReturnDate(Long returnDate) {
            mReturnDate = returnDate;
            return this;
        }

        public Asset.Builder withSerialNumber(String serialNumber) {
            mSerialNumber = serialNumber;
            return this;
        }

        public Asset.Builder withTransactionType(String transactionType) {
            mTransactionType = transactionType;
            return this;
        }

        public Asset build() {
            Asset asset = new Asset();
            asset.mAssetId = mAssetId;
            asset.mAssetModel = mAssetModel;
            asset.mAssetName = mAssetName;
            asset.mAssetNumber = mAssetNumber;
            asset.mAssetType = mAssetType;
            asset.mAssignedDate = mAssignedDate;
            asset.mAssignmentCode = mAssignmentCode;
            asset.mCost = mCost;
            asset.mDeposit = mDeposit;
            asset.mDocumentNumber = mDocumentNumber;
            asset.mExpiryDate = mExpiryDate;
            asset.mReturnDate = mReturnDate;
            asset.mSerialNumber = mSerialNumber;
            asset.mTransactionType = mTransactionType;
            return asset;
        }

    }

}
