package com.optimus.eds.ui.route.merchandize.coolerverification;

import java.io.Serializable;
import java.util.List;

/**
 * Created By apple on 4/30/19
 */
public class CoolerAsset implements Serializable {

    String code;
    String status;
    List<String> reasons;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
