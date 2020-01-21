package com.optimus.eds.ui.route.outlet;

import android.app.SearchManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.optimus.eds.BaseActivity;
import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.db.entities.Configuration;
import com.optimus.eds.db.entities.Outlet;
import com.optimus.eds.db.entities.Route;
import com.optimus.eds.model.MasterModel;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.ui.cash_memo.CashMemoActivity;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailActivity;
import com.optimus.eds.utils.CollectionUtil;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OutletListActivity extends BaseActivity implements OutletListAdapter.Callback{


    private static final int RES_CODE_DETAILS = 0x101;
    @BindView(R.id.rvOutlets)
    RecyclerView recyclerView;
    @BindView(R.id.route_spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    SearchView searchView;
    private OutletListAdapter outletListAdapter;
    private OutletListViewModel viewModel;
    int SELECTED_ROUTE_INDEX=0;
    int SELECTED_TAB=0;
    Route route;
    private String TAG=OutletListActivity.class.getSimpleName();

    public static void start(Context context) {
        Intent starter = new Intent(context, OutletListActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getID() {
        return R.layout.activity_outlet_list;
    }

    @Override
    public void created(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        setToolbar(getString(R.string.outlets));
        initOutletsAdapter();
        viewModel = ViewModelProviders.of(this).get(OutletListViewModel.class);


        viewModel.getRoutes().observe(this, routes -> {
            onRouteListLoaded(routes);
        });

        viewModel.getOutletList().observe(this, outlets -> {
            Collections.sort(outlets,(o1, o2) -> o1.getSequenceNumber().compareTo(o2.getSequenceNumber()));
            updateOutletsList(outlets);

        });

        viewModel.isLoading().observe(this,aBoolean -> {
            if(aBoolean)
                showProgress(true);
            else
                hideProgress();
        });

    }



    private void initOutletsAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        outletListAdapter = new OutletListAdapter(new ArrayList<>(),this);
        recyclerView.setAdapter(outletListAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                SELECTED_TAB=pos; // pos <1 because pjp is on zero index
                if(route!=null)
                AsyncTask.execute(() -> viewModel.loadOutletsFromDb(route.getRouteId(),SELECTED_TAB<1));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void updateOutletsList(List<Outlet> outlets) {
        outletListAdapter.populateOutlets(outlets);
        new Handler().postDelayed(() -> hideProgress(),800);

    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    @Override
    public void onOutletClick(Outlet outlet) {
        WorkStatus status = PreferenceUtil.getInstance(this).getWorkSyncData();
        if(status.getDayStarted()!=1)
        {
            showMessage(status.getDayStarted()==2 ? getString(R.string.day_already_ended):getString(R.string.have_not_started_day));
            return;
        }
        viewModel.orderTaken(outlet.getOutletId()).observe(this,aBoolean -> {
           if(aBoolean){
               CashMemoActivity.start(this,outlet.getOutletId(),RES_CODE_DETAILS);
           }else
               OutletDetailActivity.start(this,outlet.getOutletId(),route.getRouteId(),RES_CODE_DETAILS);
        });


    }


    public void onRouteListLoaded(List<Route> routes) {
        ArrayAdapter userAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, routes);
        spinner.setAdapter(userAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_ROUTE_INDEX=position;
                route = ((Route)(parent.getSelectedItem()));
                viewModel.loadOutletsFromDb(route.getRouteId(),SELECTED_TAB<1);
                AsyncTask.execute(() -> {
                    if(!viewModel.nonPjpExist(route.getRouteId())){
                        if(tabLayout.getChildCount()>1)
                            tabLayout.removeTabAt(1);
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                outletListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                try {
                    outletListAdapter.getFilter().filter(query);
                }catch (NullPointerException e){
                    Log.e("Outlet List","List is null");
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(orderUploadSuccessReceiver,new IntentFilter(Constant.ACTION_ORDER_UPLOAD));
        Log.i(TAG,"Receiver Regd");
    }

    @Override
    protected void onDestroy() {
        if (orderUploadSuccessReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(orderUploadSuccessReceiver);
            Log.i(TAG,"Receiver UnRegd");

        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(route!=null)
            viewModel.loadOutletsFromDb(route.getRouteId(),SELECTED_TAB<1);
    }

    protected BroadcastReceiver orderUploadSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==Constant.ACTION_ORDER_UPLOAD){
                MasterModel response = (MasterModel) intent.getSerializableExtra("Response");
                Toast.makeText(context, response.isSuccess()?"Order Uploaded Successfully!":response.getResponseMsg(), Toast.LENGTH_SHORT).show();
                if(route!=null)
                    viewModel.loadOutletsFromDb(route.getRouteId(),SELECTED_TAB<1);
            }
        }
    };
}
