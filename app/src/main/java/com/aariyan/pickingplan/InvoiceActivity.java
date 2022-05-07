package com.aariyan.pickingplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aariyan.pickingplan.Adapter.InvoiceAdapter;
import com.aariyan.pickingplan.Adapter.PlanAdapter;
import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Database.DatabaseAdapter;
import com.aariyan.pickingplan.Interface.GetPLanInterface;
import com.aariyan.pickingplan.Interface.RestApis;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Networking.ApiClient;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView referenceNo, planName;

    private InvoiceAdapter adapter;

    private Button invoiceBtn;
    private String qrCode = "";
    RestApis apis;

    private CompositeDisposable invoiceDisposable = new CompositeDisposable();

    private ConstraintLayout snackBarLayout;
    private ProgressBar progressBar;

    DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        apis = ApiClient.getClient().create(RestApis.class);
        databaseAdapter = new DatabaseAdapter(this);
        initUI();
        if (getIntent() != null) {
            qrCode = getIntent().getStringExtra("qrCode");
            loadInvoice(qrCode);
        }


    }

    private void loadInvoice(String qrCode) {
        progressBar.setVisibility(View.VISIBLE);
        NetworkingFeedback feedback = new NetworkingFeedback(InvoiceActivity.this, InvoiceActivity.this);
        feedback.getInvoice(new GetPLanInterface() {
            @Override
            public void gotPlan(List<PlanModel> listOfPlan) {
                if (listOfPlan.size() > 0) {
                    adapter = new InvoiceAdapter(InvoiceActivity.this, listOfPlan);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    referenceNo.setText("Reference: " + listOfPlan.get(0).getReference());
                } else {
                    Snackbar.make(snackBarLayout, "No data found!", Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(PlanActivity.this, "No Data found!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void error(String errorMessage) {
                Snackbar.make(snackBarLayout, "" + errorMessage, Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }, qrCode, snackBarLayout);
    }

    private void initUI() {

        snackBarLayout = findViewById(R.id.snackbarLayout);
        progressBar = findViewById(R.id.progressbar);
        invoiceBtn = findViewById(R.id.invoiceBtn);
        invoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitInvoice();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        referenceNo = findViewById(R.id.referenceTxtView);
        planName = findViewById(R.id.planNameTxtView);
        planName.setText("Plan Name");
    }

    private void submitInvoice() {
        invoiceDisposable.add(apis.processInvoice(qrCode, getIntent().getIntExtra("userId", 0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Throwable {
                        JSONArray root = new JSONArray(responseBody.string());
                        JSONObject single = root.getJSONObject(0);
                        String result = single.getString("result");
                        Toast.makeText(InvoiceActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InvoiceActivity.this, MainActivity.class)
                                .putExtra("userId", getIntent().getIntExtra("userId", 0))
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                        finish();
                        databaseAdapter.dropRefTable();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Toast.makeText(InvoiceActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}