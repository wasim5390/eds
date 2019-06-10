package com.optimus.eds.ui.merchandize.coolerverification;

import com.optimus.eds.db.entities.Asset;

public interface AssetVerificationStatusListener {
    void onStatusChange(Asset asset);
}
