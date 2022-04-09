package com.aariyan.pickingplan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aariyan.pickingplan.Adapter.PlanAdapter;
import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Database.DatabaseAdapter;
import com.aariyan.pickingplan.Filterable.Filter;
import com.aariyan.pickingplan.Interface.GetPLanInterface;
import com.aariyan.pickingplan.Interface.RestApis;
import com.aariyan.pickingplan.Interface.ToLoadClick;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Networking.ApiClient;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PlanActivity extends AppCompatActivity implements ToLoadClick {

    private String qrCode = "";
    private CoordinatorLayout snackBarLayout;
    PlanAdapter adapter;
    private RecyclerView recyclerView;
    private TextView referenceNo, planName;

    private ProgressBar progressBar;
    private TextView itemName;
    private EditText enteredQuantity;
    private Button saveQuantityBtn, showRemainingBtn;
    private View bottomSheet;
    private BottomSheetBehavior behavior;

    DatabaseAdapter databaseAdapter;
    List<PlanModel> filteredList = new ArrayList<>();

    private RadioButton allBtn, loadedBtn, remainingBtn;

    private Button submitBtn, invoiceBtn;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Button extrasBtn;

    private String isLongClicked;
    private CompositeDisposable invoiceDisposable = new CompositeDisposable();

    private RestApis apis;

    private String oldOrNew = "old";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        databaseAdapter = new DatabaseAdapter(PlanActivity.this);

        initUI();

        apis = ApiClient.getClient().create(RestApis.class);

        if (getIntent() != null) {
            qrCode = getIntent().getStringExtra("qrCode");
            isLongClicked = getIntent().getStringExtra(Constant.LONG_CLICK);
            progressBar.setVisibility(View.VISIBLE);
            if (qrCode.equals("nothing")) {
                //Snackbar.make(snackBarLayout, "You didn't scan anything!", Snackbar.LENGTH_SHORT).show();
            } else {
                //progressBar.setVisibility(View.VISIBLE);
                //Drop the table before getting the data:
                databaseAdapter.dropPlanTable();
                //referenceNo.setText("Reference: " + qrCode);
            }
            if (getIntent().hasExtra(Constant.LONG_CLICK)) {
                if (isLongClicked.equals(Constant.YES)) {
                    oldOrNew = "new";
                    loadInvoice(qrCode);
                    invoiceBtn.setVisibility(View.VISIBLE);
                    extrasBtn.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                }
            } else {
                oldOrNew = "old";
                loadPlan(qrCode);
                invoiceBtn.setVisibility(View.GONE);
                submitBtn.setVisibility(View.VISIBLE);
                extrasBtn.setVisibility(View.VISIBLE);
            }

        }
    }

    private void loadInvoice(String qrCode) {
        NetworkingFeedback feedback = new NetworkingFeedback(PlanActivity.this, PlanActivity.this);
        feedback.getInvoice(new GetPLanInterface() {
            @Override
            public void gotPlan(List<PlanModel> listOfPlan) {
                if (listOfPlan.size() > 0) {
                    adapter = new PlanAdapter(PlanActivity.this, listOfPlan, PlanActivity.this, Constant.fromInvoice);
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

    private void loadPlan(String qrCode) {
        NetworkingFeedback feedback = new NetworkingFeedback(PlanActivity.this, PlanActivity.this);
        feedback.getPlan(new GetPLanInterface() {
            @Override
            public void gotPlan(List<PlanModel> listOfPlan) {
                if (listOfPlan.size() > 0) {
                    adapter = new PlanAdapter(PlanActivity.this, listOfPlan, PlanActivity.this, Constant.fromPlan);
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

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(PlanActivity.this).create();
        alertDialog.setTitle("Alert Message");
        alertDialog.setMessage("Are you sure you want to return to the main page?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void initUI() {
//        showRemainingBtn = findViewById(R.id.showRemainingBtn);
//        showRemainingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadFilteredData();
//            }
//        });

        invoiceBtn = findViewById(R.id.invoiceBtn);
        invoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitInvoice();
            }
        });

        extrasBtn = findViewById(R.id.showRemainingBtn);
        extrasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanActivity.this, ExtrasActivity.class)
                        .putExtra("code", qrCode));
            }
        });

        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanActivity.this, UploadActivity.class)
                        .putExtra("code", qrCode)
                        //.putExtra("code", qrCode)
                        .putExtra("userId", getIntent().getIntExtra("userId", 1))
                );
            }
        });

        allBtn = findViewById(R.id.allRadioBtn);
        remainingBtn = findViewById(R.id.remainingRadioBtn);
        loadedBtn = findViewById(R.id.loadedRadioBtn);

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPlan("nothing");
                allBtn.setChecked(true);
                remainingBtn.setChecked(false);
                loadedBtn.setChecked(false);
            }
        });

        remainingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFilteredData(1);
                allBtn.setChecked(false);
                remainingBtn.setChecked(true);
                loadedBtn.setChecked(false);
            }
        });

        loadedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFilteredData(0);
                allBtn.setChecked(false);
                remainingBtn.setChecked(false);
                loadedBtn.setChecked(true);
            }
        });

        snackBarLayout = findViewById(R.id.snackBarLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressbar);
        bottomSheet = findViewById(R.id.bottomSheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        itemName = findViewById(R.id.itemName);
        enteredQuantity = findViewById(R.id.enterQuantityEdtText);
        saveQuantityBtn = findViewById(R.id.saveQuantity);

        referenceNo = findViewById(R.id.referenceTxtView);
        planName = findViewById(R.id.planNameTxtView);
        planName.setText("Plan Name");
    }

    private void submitInvoice() {
        invoiceDisposable.add(apis.processInvoice(qrCode,getIntent().getIntExtra("userId", 0))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Throwable {
                JSONArray root = new JSONArray(responseBody.string());
                JSONObject single = root.getJSONObject(0);
                String result = single.getString("result");
                Toast.makeText(PlanActivity.this, ""+result, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PlanActivity.this, BarcodeActivity.class)
                        .putExtra("userId", getIntent().getIntExtra("userId", 0)));
                databaseAdapter.dropRefTable();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Throwable {
                Toast.makeText(PlanActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));
    }

//    private void loadFilteredData(int flag) {
//        NetworkingFeedback feedback = new NetworkingFeedback(PlanActivity.this, PlanActivity.this);
//        feedback.getPlan(new GetPLanInterface() {
//            @Override
//            public void gotPlan(List<PlanModel> listOfPlan) {
//                if (listOfPlan.size() > 0) {
////                    if (flag == 0) {
////                        filteredList.clear();
////                        filteredList = new Filter(PlanActivity.this).getFilteredForLoaded(listOfPlan);
////                        Toast.makeText(PlanActivity.this, listOfPlan.size()+"->"+filteredList.size(), Toast.LENGTH_SHORT).show();
////                    } else {
////                        filteredList.clear();
////                        filteredList = new Filter(PlanActivity.this).getFilteredForRemaining(listOfPlan);
////                    }
//
//                    filteredList.clear();
//                    filteredList = new Filter(PlanActivity.this).getFilteredData(listOfPlan, flag);
//
//                    adapter = new PlanAdapter(PlanActivity.this, filteredList, PlanActivity.this);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Snackbar.make(snackBarLayout, "No data found!", Snackbar.LENGTH_SHORT).show();
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void error(String errorMessage) {
//                Snackbar.make(snackBarLayout, "" + errorMessage, Snackbar.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.GONE);
//            }
//        }, qrCode, snackBarLayout);
//    }

    private void loadFilteredData(int flag) {
        filteredList.clear();
        List<PlanModel> list = new NetworkingFeedback(PlanActivity.this, PlanActivity.this).getPlanForFilter();
        filteredList = new Filter(PlanActivity.this).getFilteredData(list, flag);
        adapter = new PlanAdapter(PlanActivity.this, filteredList, PlanActivity.this, Constant.fromPlan);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(PlanModel model) {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        itemName.setText("Item Name: " + model.getDescription());
        enteredQuantity.setText(model.getToLoad());
        saveQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalQuantity = enteredQuantity.getText().toString();
                if (TextUtils.isEmpty(finalQuantity)) {
                    enteredQuantity.setError("Quantity is empty!");
                    enteredQuantity.requestFocus();
                    return;
                }

                long id = databaseAdapter.updatePlanToLoad(model.getDescription(), qrCode, finalQuantity, model.getStorename(), model.getLineNos(), 1);
                if (id > 0) {
                    Snackbar.make(snackBarLayout, "Updated To " + finalQuantity, Snackbar.LENGTH_SHORT).show();
                    loadPlan("nothing");
                } else {
                    Snackbar.make(snackBarLayout, "Failed to update", Snackbar.LENGTH_SHORT).show();
                }

                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

}