package com.aariyan.pickingplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aariyan.pickingplan.Adapter.PostAdapter;
import com.aariyan.pickingplan.Database.DatabaseAdapter;
import com.aariyan.pickingplan.Filterable.Filter;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Model.PostModel;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private DatabaseAdapter databaseAdapter;
    private String referenceCode;
    private Filter filter;
    //private List<PostModel> listOfPostData = new ArrayList<>();
    private List<PlanModel> listOfPostData = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button submitBtn;
    private PostAdapter adapter;

    private ConstraintLayout snackBarLayout;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        databaseAdapter = new DatabaseAdapter(this);
        filter = new Filter(this);

        if (getIntent() != null) {
            referenceCode = getIntent().getStringExtra("code");
            userID = getIntent().getIntExtra("userId", 1);
        }

        initUI();

        loadData();
    }

    private void loadData() {
        listOfPostData.clear();
        //listOfPostData = filter.getPostData(databaseAdapter.getPlansByReference(referenceCode));
        listOfPostData = filter.getPostData(databaseAdapter.getPlans());
        //Toast.makeText(this, ""+listOfPostData.size(), Toast.LENGTH_SHORT).show();

        adapter = new PostAdapter(this, listOfPostData);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        if (listOfPostData.size() > 0) {
//            adapter = new PostAdapter(this, listOfPostData);
//            recyclerView.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
//        } else {
//            Snackbar.make(snackBarLayout, "Not enough data!", Snackbar.LENGTH_SHORT).show();
//        }

    }

    private void initUI() {

        snackBarLayout = findViewById(R.id.snackBarLayout);
        recyclerView = findViewById(R.id.uploadRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        submitBtn = findViewById(R.id.submitBtns);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDataOnServer();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void postDataOnServer() {
        if (isNetworkConnected()) {
            if (listOfPostData.size() > 0) {
                NetworkingFeedback networkingFeedback =
                        new NetworkingFeedback(UploadActivity.this, UploadActivity.this);

                networkingFeedback.postDataOnServer(listOfPostData, snackBarLayout, userID);
            } else {
                Snackbar.make(snackBarLayout, "Not enough data!", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(snackBarLayout, "No Internet Connection!", Snackbar.LENGTH_SHORT).show();
        }

    }
}