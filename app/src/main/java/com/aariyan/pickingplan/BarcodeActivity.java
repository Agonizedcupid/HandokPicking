package com.aariyan.pickingplan;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aariyan.pickingplan.Adapter.RefAdapter;
import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Interface.RefInterface;
import com.aariyan.pickingplan.Model.RefModel;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class BarcodeActivity extends AppCompatActivity {

    private Button tapToScan, getPlan, continueWithMyPlay;
    private TextView scannedCode;
    private ConstraintLayout snackBarLayout;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private ImageView backPressed;

    public static String name = "";
    public static int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            userId = getIntent().getIntExtra("userId", 0);
            Constant.userName = name;
            Constant.usrId = userId;
        }

        initUI(savedInstanceState);
    }


    private void initUI(Bundle savedInstanceState) {
        snackBarLayout = findViewById(R.id.snackBarLayout);
        tapToScan = findViewById(R.id.tapToScanCode);
        scannedCode = findViewById(R.id.scannedCode);
        getPlan = findViewById(R.id.getPlan);
        continueWithMyPlay = findViewById(R.id.continueWithMyPLan);

        progressBar = findViewById(R.id.pBar);
        backPressed = findViewById(R.id.backPressed);
        backPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BarcodeActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        recyclerView = findViewById(R.id.refRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadReference();

        tapToScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions options = new ScanOptions().setCaptureActivity(ScannerActivity.class);
                barcodeLauncher.launch(options);
            }
        });

        //It will show the default data from local DB:
        getPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarcodeActivity.this, PlanActivity.class);
                intent.putExtra("qrCode", "nothing");
                intent.putExtra("userId", getIntent().getIntExtra("userId", 1));
                startActivity(intent);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //This will pull the new data from server:
        continueWithMyPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = scannedCode.getText().toString();

                if (TextUtils.isEmpty(code)) {
                    Snackbar.make(snackBarLayout, "you didn't scan anything", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(BarcodeActivity.this).create();
                alertDialog.setTitle("Alert Message");
                alertDialog.setMessage("Are you sure you want to get new Data?");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(BarcodeActivity.this, PlanActivity.class);
                        intent.putExtra("qrCode", code);
                        intent.putExtra("userId", getIntent().getIntExtra("userId", 1));
                        startActivity(intent);
                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(BarcodeActivity.this, PlanActivity.class);
                        intent.putExtra("qrCode", "nothing");
                        intent.putExtra("userId", getIntent().getIntExtra("userId", 1));
                        startActivity(intent);
                    }
                });

                alertDialog.show();

            }
        });
    }

    private void loadReference() {
        int id = getIntent().getIntExtra("userId", 1);
        progressBar.setVisibility(View.VISIBLE);
        NetworkingFeedback networkingFeedback = new NetworkingFeedback(BarcodeActivity.this,BarcodeActivity.this);
        networkingFeedback.getReferenceFromServer(id, new RefInterface() {
            @Override
            public void onSuccess(List<RefModel> list) {
                RefAdapter refAdapter = new RefAdapter(BarcodeActivity.this, list);
                recyclerView.setAdapter(refAdapter);
                refAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(BarcodeActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(BarcodeActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(BarcodeActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(BarcodeActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    scannedCode.setText(result.getContents());
                    Log.d("SCANNED_RESULT", "" + result.getContents());
                }
            });

}