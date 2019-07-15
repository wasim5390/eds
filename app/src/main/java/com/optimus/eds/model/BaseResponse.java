package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sidhu on 4/11/2018.
 */

public class BaseResponse implements Serializable {


    public void setSuccess(Boolean success) {
        this.success = success;
    }

    private Boolean success;
    private String errorMessage;

    public String getResponseMsg() {
        return errorMessage;
    }

    public void setResponseMsg(String responseMsg) {
        this.errorMessage = responseMsg;
    }
    public Boolean isSuccess() {
        return success;
    }
}
