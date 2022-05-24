package com.aariyan.pickingplan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Interface.GetTicketInterface;
import com.aariyan.pickingplan.Interface.RestApis;
import com.aariyan.pickingplan.Networking.ApiClient;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ExtrasActivity extends AppCompatActivity {

    private EditText checkerName, dunnages, straps, pallets, plasticComeres, tarps, stans, trail, belts, nets;
    private RadioButton completeYes, completeNo, secureYes, secureNo;
    private String complete = "", secure = "";
    private Button saveBtn;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    RestApis apis;

    private RequestQueue requestQueue;

    private ProgressBar progressBar;

    private Spinner ticketSpinner, trailorOneSpinner, trailorTwoSpinner;
    private String ticketString="",trailorOneString = "", trailorTwoString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extras);
        apis = ApiClient.getClient().create(RestApis.class);
        requestQueue = Volley.newRequestQueue(this);
        initUI();
    }

    private void initUI() {
        checkerName = findViewById(R.id.checkerName);
        dunnages = findViewById(R.id.dunnages);
        straps = findViewById(R.id.straps);
        pallets = findViewById(R.id.pallets);
        plasticComeres = findViewById(R.id.plasticComers);
        tarps = findViewById(R.id.tarps);
        stans = findViewById(R.id.stans);
        trail = findViewById(R.id.trailorNo);
        belts = findViewById(R.id.belts);
        nets = findViewById(R.id.nets);

        secureYes = findViewById(R.id.yesSecureBtn);
        secureNo = findViewById(R.id.noSecureBtn);

        completeYes = findViewById(R.id.yesCompleteBtn);
        completeNo = findViewById(R.id.noCompleteBtn);
        saveBtn = findViewById(R.id.saveBtn);

        ticketSpinner = findViewById(R.id.ticketSpinner);
        trailorOneSpinner = findViewById(R.id.trailorOneSpinner);
        trailorTwoSpinner = findViewById(R.id.trailorTwoSpinner);

        progressBar = findViewById(R.id.pBar);

        completeYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complete = "YES";
                completeYes.setChecked(true);
                completeNo.setChecked(false);
            }
        });

        completeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complete = "NO";
                completeYes.setChecked(false);
                completeNo.setChecked(true);
            }
        });

        secureYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secure = "YES";
                secureYes.setChecked(true);
                secureNo.setChecked(false);
            }
        });

        secureNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secure = "NO";
                secureYes.setChecked(false);
                secureNo.setChecked(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        setUpSpinner();

    }

    private void setUpSpinner() {

        NetworkingFeedback networkingFeedback = new NetworkingFeedback(this, this);
        networkingFeedback.getTicket(new GetTicketInterface() {
            @Override
            public void getTicket(List<String> listOfTicket) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ExtrasActivity.this, android.R.layout.simple_spinner_item,listOfTicket);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                ticketSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(ExtrasActivity.this, ""+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        ticketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ticketString = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(ExtrasActivity.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Trailor One:
        networkingFeedback.getTrailor(new GetTicketInterface() {
            @Override
            public void getTicket(List<String> listOfTrailor) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ExtrasActivity.this, android.R.layout.simple_spinner_item,listOfTrailor);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                trailorOneSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(ExtrasActivity.this, ""+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        trailorOneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trailorOneString = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(ExtrasActivity.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        networkingFeedback.getTrailor(new GetTicketInterface() {
            @Override
            public void getTicket(List<String> listOfTrailor) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ExtrasActivity.this, android.R.layout.simple_spinner_item,listOfTrailor);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                trailorTwoSpinner.setAdapter(dataAdapter);
            }

            @Override
            public void error(String errorMessage) {
                Toast.makeText(ExtrasActivity.this, ""+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        trailorTwoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trailorTwoString = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(ExtrasActivity.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void validate() {
        if (TextUtils.isEmpty(checkerName.getText().toString())) {
            checkerName.setError("Please fill this");
            checkerName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dunnages.getText().toString())) {
            dunnages.setError("Please fill this");
            dunnages.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(straps.getText().toString())) {
            straps.setError("Please fill this");
            straps.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pallets.getText().toString())) {
            pallets.setError("Please fill this");
            pallets.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(plasticComeres.getText().toString())) {
            plasticComeres.setError("Please fill this");
            plasticComeres.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(tarps.getText().toString())) {
            tarps.setError("Please fill this");
            tarps.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stans.getText().toString())) {
            stans.setError("Please fill this");
            stans.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(trail.getText().toString())) {
            trail.setError("Please fill this");
            trail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(belts.getText().toString())) {
            belts.setError("Please fill this");
            belts.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nets.getText().toString())) {
            nets.setError("Please fill this");
            nets.requestFocus();
            return;
        }

        if (complete.equals("")) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (secure.equals("")) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        saveBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Do the API staff:
//        compositeDisposable.add(apis.postExtras("" + checkerName.getText().toString(), "" + dunnages.getText().toString(),
//                "" + pallets.getText().toString(), "" + straps.getText().toString(), "" + plasticComeres.getText().toString(),
//                "" + tarps.getText().toString(), "" + stans.getText().toString(), "" + trail.getText().toString(),
//                "" + belts.getText().toString(), "" + nets.getText().toString(), "" + complete, "" + secure,
//                "" + getIntent().getStringExtra("code"), "" + Constant.usrId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Throwable {
//                        Log.e("TAG", "accept: "+responseBody.string() );
//                        JSONArray root = new JSONArray(responseBody.string());
//                        JSONObject ob = root.getJSONObject(0);
//                        String result = ob.getString("result");
//                        Toast.makeText(ExtrasActivity.this, ""+result, Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(ExtrasActivity.this, BarcodeActivity.class)
//                        .putExtra("userId", Constant.usrId));
//                        finish();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Throwable {
//                        Toast.makeText(ExtrasActivity.this, "Failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }));
//        compositeDisposable.add(apis.postExtras("" + checkerName.getText().toString(), Integer.parseInt(dunnages.getText().toString()),
//                Integer.parseInt(pallets.getText().toString()), Integer.parseInt(straps.getText().toString()),
//                Integer.parseInt(plasticComeres.getText().toString()),
//                Integer.parseInt(tarps.getText().toString()),
//                Integer.parseInt(stans.getText().toString()),
//                "" + trail.getText().toString(),
//                Integer.parseInt(belts.getText().toString()),
//                Integer.parseInt(nets.getText().toString()), "" + complete, "" + secure,
//                //"" + getIntent().getStringExtra("code"),
//                "1632087778dehfV-l-Za",
//                Constant.usrId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Throwable {
//                        Log.d("RESPONSE", responseBody.string());
//                        JSONArray root = new JSONArray(responseBody.string());
////                        for (int i=0; i<root.length(); i++) {
////                            JSONObject ob = root.getJSONObject(i);
////                            String result = ob.getString("result");
////                            Log.d("RESPONSE", responseBody.string());
////                        }
//                        Log.d("RESPONSE", root.toString());
//                        //Log.d("RESPONSE", responseBody.string());
//                        Toast.makeText(ExtrasActivity.this, ""+root, Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(ExtrasActivity.this, BarcodeActivity.class)
//                        .putExtra("userId", Constant.usrId));
//                        finish();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Throwable {
//                        Log.d("RESPONSE", throwable.getMessage());
//                        Toast.makeText(ExtrasActivity.this, "Failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }));

        String URL = Constant.BASE_URL + "PostExtras.php";

        StringRequest arrayRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("TAG", "onResponse: "+response.toString());
                    JSONArray root = new JSONArray(response);
                    JSONObject single = root.getJSONObject(0);
                    String result = single.getString("result");
                    Toast.makeText(ExtrasActivity.this, "" + result, Toast.LENGTH_SHORT).show();
                   //startActivity(new Intent(ExtrasActivity.this, BarcodeActivity.class)
                    startActivity(new Intent(ExtrasActivity.this, MainActivity.class)
                            .putExtra("userId", Constant.usrId));
                    saveBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    finish();
                }catch (Exception e) {
                    Toast.makeText(ExtrasActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveBtn.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                saveBtn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExtrasActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG", "onErrorResponse: "+e.getMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("strCheckerName",""+checkerName.getText().toString());
                map.put("intDunnages",""+dunnages.getText().toString());
                map.put("intPallets",""+pallets.getText().toString());
                map.put("intStraps",""+straps.getText().toString());
                map.put("intPlasticCorners",""+ plasticComeres.getText().toString());
                map.put("intTarps",""+ tarps.getText().toString());
                map.put("intStans",""+stans.getText().toString());
                map.put("strTrailorNo",""+trail.getText().toString());
                map.put("intBelts",""+ belts.getText().toString());
                map.put("intNets","" + nets.getText().toString());
                map.put("strLoadComplete",""+ complete);
                map.put("strLoadSecured",""+ secure);
                map.put("trailorone",""+ trailorOneString);
                map.put("trailortwo",""+ trailorTwoString);
                map.put("ticket",""+ ticketString);
                map.put("reference",""+ getIntent().getStringExtra("code"));
                map.put("userId",""+ Constant.usrId);
                return map;
            }
        };

        requestQueue.add(arrayRequest);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}