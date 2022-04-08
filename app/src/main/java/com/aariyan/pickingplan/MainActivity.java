package com.aariyan.pickingplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Database.S_Preferences;
import com.aariyan.pickingplan.Interface.LogInInterface;
import com.aariyan.pickingplan.Model.AuthenticationModel;
import com.aariyan.pickingplan.Networking.NetworkingFeedback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Instance variable of logIn btn:
    private Button logInBtn;
    //Instance variable of Edit Text of code entering:
    private EditText enterCodeEdtText;

    //Instance variable of Constraint layout for SnackBar:
    private CoordinatorLayout snackBarLayout;

    private ProgressBar progressBar;

    private FloatingActionButton closeApp;

    private EditText ipField, userId;
    private Button saveBtn, exitBtn;

    private View ipBottomSheet;
    BottomSheetBehavior ipBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constant.BASE_URL = getURL();

        //Instantiate UI variables
        initUI();
    }

    private void initUI() {
        //instantiating the log In btn:
        logInBtn = findViewById(R.id.logInBtn);
        //Instantiating the Edit Text Field for entering the code:
        enterCodeEdtText = findViewById(R.id.enterCode);

        //Instantiating the snackBar layout:
        snackBarLayout = findViewById(R.id.snackBarLayout);

        progressBar = findViewById(R.id.progressbar);

        //instantiating the onClick for button listener, later we will implement the interface for triggering:
        logInBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.saveBtn);
        exitBtn = findViewById(R.id.exitBtn);

        ipField = findViewById(R.id.ipField);

        closeApp = findViewById(R.id.closeTheApp);

        ipBottomSheet = findViewById(R.id.bottomSheetForIp);
        ipBehavior = BottomSheetBehavior.from(ipBottomSheet);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ipField.getText().toString().endsWith("/")) {
                    ipBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    S_Preferences sharedPreferences = new S_Preferences(MainActivity.this);
                    sharedPreferences.saveBaserUrl(ipField.getText().toString(), "root");

                    Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    Constant.BASE_URL = getURL();
                    Toast.makeText(MainActivity.this, "" + Constant.BASE_URL, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Ip should end with a / (Forward slash)", Toast.LENGTH_SHORT).show();
                }

            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ipBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.app_close_dialog, null);
        TextView setUps = view.findViewById(R.id.setUps);
        TextView yes = view.findViewById(R.id.yes);
        TextView no = view.findViewById(R.id.no);

        setUps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                //saving the value on shared preference:
                ipBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                ipField.setText(getURL(), TextView.BufferType.EDITABLE);
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                System.exit(0);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public String getURL() {
        S_Preferences sharedPreferences = new S_Preferences(MainActivity.this);
        //Constant.BASE_URL = sharedPreferences.getBaseUrl("root");
        return sharedPreferences.getBaseUrl("root");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.logInBtn:
                logInValidation();
                break;
        }
    }

    //Checking the log-In validation whether the entered data is correct or not:
    private void logInValidation() {
        progressBar.setVisibility(View.VISIBLE);
        Constant.BASE_URL = getURL();
        Log.d("URLs", Constant.BASE_URL);

        //Entered value from edit Text:
        String enteredPinCode = enterCodeEdtText.getText().toString().trim();
        //checking, String is empty (User inputted any code or not):
        if (TextUtils.isEmpty(enteredPinCode)) {
            //if the pin is empty or no pin inputted by the use:
            enterCodeEdtText.setError("Please enter pin code to continue!");
            enterCodeEdtText.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        NetworkingFeedback networking = new NetworkingFeedback(MainActivity.this, MainActivity.this);
        networking.postLogInResponse(new LogInInterface() {
            @Override
            public void checkLogIn(List<AuthenticationModel> list) {
                if (list.size() > 0) {
                    Intent barcodeIntent = new Intent(MainActivity.this, BarcodeActivity.class);
                    barcodeIntent.putExtra("name", list.get(0).getPickingTeams());
                    barcodeIntent.putExtra("userId", list.get(0).getUserID());
                    startActivity(barcodeIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Snackbar.make(snackBarLayout, "Invalid Credential!", Snackbar.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                Snackbar.make(snackBarLayout, "" + errorMessage, Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }, enteredPinCode);
    }
}