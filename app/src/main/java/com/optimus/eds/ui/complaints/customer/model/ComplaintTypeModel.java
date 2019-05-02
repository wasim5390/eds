package com.optimus.eds.ui.complaints.customer.model;

import com.optimus.eds.ui.complaints.customer.model.ComplaintReasonModel;
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup;
import java.util.List;

/**
 * Created By apple on 5/1/19
 */
public class ComplaintTypeModel extends MultiCheckExpandableGroup {

    public ComplaintTypeModel(String title, List<ComplaintReasonModel> items) {
        super(title, items);
    }
}
