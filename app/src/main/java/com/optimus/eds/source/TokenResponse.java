
package com.optimus.eds.source;

import com.google.gson.annotations.SerializedName;
import com.optimus.eds.model.BaseResponse;


public class TokenResponse extends BaseResponse {

    @SerializedName("access_token")
    private String mAccessToken;
    @SerializedName("expires_in")
    private Long mExpiresIn;
    @SerializedName("token_type")
    private String mTokenType;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public Long getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        mExpiresIn = expiresIn;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }

}
