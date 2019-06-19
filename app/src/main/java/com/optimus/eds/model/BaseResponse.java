package com.optimus.eds.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sidhu on 4/11/2018.
 */

public class BaseResponse {

    private String message;
    private Boolean success;

    public String getResponseMsg() {
        return message;
    }

    public void setResponseMsg(String responseMsg) {
        this.message = responseMsg;
    }
    public Boolean isSuccess() {
        return success;
    }
}
