package com.app.syspoint.ui;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.LogSync;
import com.app.syspoint.R;
import com.app.syspoint.SincAdapter;
import com.app.syspoint.db.bean.LogSyncGetBean;
import com.app.syspoint.db.dao.LogSyncGetDao;
import com.app.syspoint.http.Data;
import com.app.syspoint.utils.ItemAnimation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SincCloudActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout lyt_progress;
    private ProgressBar progress_indeterminate;
    private LinearLayout lyt_no_connection;
    private List<LogSyncGetBean> mData;
    private List<LogSync> logSyncList;
    private SincAdapter mAdapter;
    private int animation_type = ItemAnimation.FADE_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinc_cloud);

        this.logSyncList = new ArrayList<>();
        this.initToolBar();
        lyt_no_connection = findViewById(R.id.lyt_no_connection);
        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.GONE);
        progress_indeterminate = findViewById(R.id.progress_indeterminate);
        progress_indeterminate.setVisibility(View.GONE);
        this.initRecyclerViews();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sync);
        toolbar.setTitle("Sincronización");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_500));
        }
    }

    protected void initRecyclerViews() {


        mData = (List<LogSyncGetBean>)(List<?>) new LogSyncGetDao().list();

        VisibilityControls();

        recyclerView =  findViewById(R.id.recyclerView_log);
        recyclerView.setHasFixedSize(true);

        /*** ----- Manejador ------ ****/
        final LinearLayoutManager manager = new LinearLayoutManager(SincCloudActivity.this);
        recyclerView.setLayoutManager(manager);


        mAdapter = new SincAdapter(mData, animation_type);
        recyclerView.setAdapter(mAdapter);
    }


    private void VisibilityControls(){

        if (mData.size() > 0) {
            lyt_no_connection.setVisibility(View.GONE);
            lyt_progress.setVisibility(View.GONE);
            progress_indeterminate.setVisibility(View.GONE);
        }else {
            progress_indeterminate.setVisibility(View.GONE);
            lyt_no_connection.setVisibility(View.VISIBLE);
            lyt_progress.setVisibility(View.GONE);
        }
    }

    public class  getDataCloud extends AsyncTask<Call, Void, String>{
        Response<Data> response;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Call... calls) {
            return null;
        }
    }



}