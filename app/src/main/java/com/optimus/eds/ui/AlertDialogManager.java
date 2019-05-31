package com.optimus.eds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.optimus.eds.R;
import com.optimus.eds.model.CustomObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AlertDialogManager {
    private static final AlertDialogManager ourInstance = new AlertDialogManager();

    public static AlertDialogManager getInstance() {
        return ourInstance;
    }

    private AlertDialogManager() {
    }


    /**
     * Pass List of [{@CustomObject}] and listener for accepting any from list
     * @param context
     * @param listener
     * @param options
     */
    public void showListAlertDialog(Context context,ListAlertItemClickListener listener, List<CustomObject> options) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_discount_tag);
        builderSingle.setTitle(context.getString(R.string.available_promotions));
        final ArrayAdapter<CustomObject> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(options);

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            listener.onAlertItemClick(arrayAdapter.getItem(which));

        });
        builderSingle.setPositiveButton("Ok", (dialog1, which1) -> dialog1.dismiss());
        builderSingle.show();
    }

    /**
     * Provide current location and destination location for distance measurement
     * @param context
     * @param currentLocation
     * @param outletLocation
     */
    public void showLocationMissMatchAlertDialog(Context context, Location currentLocation,Location outletLocation) {
        double distance = currentLocation.distanceTo(outletLocation);
        BigDecimal dis = new BigDecimal(distance).setScale(2,RoundingMode.HALF_UP);
        LayoutInflater inflater  = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.location_mismatch_dialog,null);
        ((TextView)view.findViewById(R.id.tvOutletLocation)).setText(outletLocation.getLatitude()+" / "+outletLocation.getLongitude());
        ((TextView)view.findViewById(R.id.tvYourLocation)).setText(currentLocation.getLatitude()+" / "+currentLocation.getLongitude());
        ((TextView)view.findViewById(R.id.tvDistance)).setText(String.valueOf(dis).concat(" meters"));
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        builderSingle.setTitle(context.getString(R.string.incorrect_location));
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Ok", (dialog1, which1) -> dialog1.dismiss());
        builderSingle.show();
    }

    public interface ListAlertItemClickListener{
        void onAlertItemClick(CustomObject object);
    }

}
