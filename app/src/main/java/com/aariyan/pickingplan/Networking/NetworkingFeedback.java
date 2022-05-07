package com.aariyan.pickingplan.Networking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.aariyan.pickingplan.BarcodeActivity;
import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Database.DatabaseAdapter;
import com.aariyan.pickingplan.Interface.GetPLanInterface;
import com.aariyan.pickingplan.Interface.LogInInterface;
import com.aariyan.pickingplan.Interface.RefInterface;
import com.aariyan.pickingplan.Interface.RestApis;
import com.aariyan.pickingplan.MainActivity;
import com.aariyan.pickingplan.Model.AuthenticationModel;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Model.PostModel;
import com.aariyan.pickingplan.Model.RefModel;
import com.aariyan.pickingplan.PlanActivity;
import com.aariyan.pickingplan.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class NetworkingFeedback {

    private Context context;
    ApiClient apiClient;
    RestApis apis;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<AuthenticationModel> listOfUsers;
    private List<PlanModel> listOfPlans;
    DatabaseAdapter databaseAdapter;
    private Activity activity;
    private RequestQueue requestQueue;
    private List<RefModel> listOfRef = new ArrayList<>();

    CompositeDisposable refDisposable = new CompositeDisposable();

    public NetworkingFeedback(Context context, Activity activity) {
        this.context = context;
        apis = ApiClient.getClient().create(RestApis.class);
        listOfUsers = new ArrayList<>();
        listOfPlans = new ArrayList<>();
        databaseAdapter = new DatabaseAdapter(context);
        this.activity = activity;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void getReferenceFromServer(int id, RefInterface refInterface) {
        listOfRef.clear();
        listOfRef = databaseAdapter.getRefById(id);
        if (listOfRef.size() > 0) {
            refInterface.onSuccess(listOfRef);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Showing from local database", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        } else {
            databaseAdapter.dropRefTable();
            refDisposable.add(apis.getReference(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Throwable {
                            JSONArray root = new JSONArray(responseBody.string());
//                            Log.d("RESPONSES", responseBody.string());
//                            Log.d("RESPONSES",root.toString());
                            if (root.length() > 0) {
                                listOfRef.clear();
                                for (int i = 0; i < root.length(); i++) {
                                    JSONObject single = root.getJSONObject(i);
                                    int intAutoPickingHeader = single.getInt("intAutoPickingHeader");
                                    String strUnickReference = single.getString("strUnickReference");
                                    String strPickingNickname = single.getString("strPickingNickname");

                                    RefModel model = new RefModel(intAutoPickingHeader, strUnickReference, strPickingNickname);
                                    listOfRef.add(model);

                                }
                                refInterface.onSuccess(listOfRef);
                                insertRef(listOfRef, id);

                            } else {
                                refInterface.onError("No Reference Found!");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            refInterface.onError("" + throwable.getMessage());
                        }
                    }));
        }
    }

    public void postLogInResponse(LogInInterface logInInterface, String pinCode) {
        String URL = Constant.BASE_URL + "users.php";
        Log.d("RESPONSE", URL);
        /*JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.POST, URL
                , null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray root) {
                Log.d("RESPONSE", root.toString());
                try {
                    listOfUsers.clear();
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject single = root.getJSONObject(i);
                        int userId = single.getInt("UserId");
                        int locationId = single.getInt("LocationId");
                        String pickingTeams = single.getString("strPickingTeams");

                        AuthenticationModel model = new AuthenticationModel(userId, locationId, pickingTeams);
                        listOfUsers.add(model);
                    }
                    logInInterface.checkLogIn(listOfUsers);
                } catch (Exception throwable) {
                    logInInterface.onError(throwable.getMessage());
                    Log.d("RESPONSE", throwable.getMessage());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError throwable) {
                logInInterface.onError(throwable.getMessage());
                Log.d("RESPONSE", throwable.getMessage());
            }

        }) {
            @Override
            public byte[] getBody() {
                Map<String, String> map = new HashMap<>();
                map.put("pincode", pinCode);
                return new Gson().toJson(map).getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(arrayRequest);*/

        StringRequest mStringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseBody) {
                        try {
                            JSONArray root = new JSONArray(responseBody);
                            listOfUsers.clear();
                            for (int i = 0; i < root.length(); i++) {
                                JSONObject single = root.getJSONObject(i);
                                int userId = single.getInt("UserId");
                                int locationId = single.getInt("LocationId");
                                String pickingTeams = single.getString("strPickingTeams");

                                AuthenticationModel model = new AuthenticationModel(userId, locationId, pickingTeams);
                                listOfUsers.add(model);
                            }
                            logInInterface.checkLogIn(listOfUsers);
                        } catch (Exception throwable) {
                            logInInterface.onError(throwable.getMessage());
                            Log.d("RESPONSE", throwable.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError throwable) {
                        logInInterface.onError(throwable.getMessage());
                        Log.e("TAGEEEE", "onErrorResponse: " + throwable.getMessage());
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("pincode", pinCode);
                return map;
            }
        };

        requestQueue.add(mStringRequest);

//        Call<ResponseBody> call = apis.logInResponse(pinCode);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseBody) {
//                try {
//                    JSONArray root = new JSONArray(responseBody.body().string());
//                    listOfUsers.clear();
//                    for (int i = 0; i < root.length(); i++) {
//                        JSONObject single = root.getJSONObject(i);
//                        int userId = single.getInt("UserId");
//                        int locationId = single.getInt("LocationId");
//                        String pickingTeams = single.getString("strPickingTeams");
//
//                        AuthenticationModel model = new AuthenticationModel(userId, locationId, pickingTeams);
//                        listOfUsers.add(model);
//                    }
//                    logInInterface.checkLogIn(listOfUsers);
//                } catch (Exception throwable) {
//                    logInInterface.onError(throwable.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
//                logInInterface.onError(throwable.getMessage());
//            }
//        });
    }

//    public void postLogInResponse(LogInInterface logInInterface, String pinCode) {
//        Log.d("URL", Constant.BASE_URL);
//        compositeDisposable.add(apis.logInResponse(pinCode)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Throwable {
//                        JSONArray root = new JSONArray(responseBody.string());
//                        listOfUsers.clear();
//                        for (int i = 0; i < root.length(); i++) {
//                            JSONObject single = root.getJSONObject(i);
//                            int userId = single.getInt("UserId");
//                            int locationId = single.getInt("LocationId");
//                            String pickingTeams = single.getString("strPickingTeams");
//
//                            AuthenticationModel model = new AuthenticationModel(userId, locationId, pickingTeams);
//                            listOfUsers.add(model);
//                        }
//                        logInInterface.checkLogIn(listOfUsers);
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Throwable {
//                        logInInterface.onError(throwable.getMessage());
//                    }
//                }));
//    }

    public List<PlanModel> getPlanForFilter() {
        return listOfPlans = databaseAdapter.getPlans();
    }

    public void getPlan(GetPLanInterface getPLanInterface, String qrCode, CoordinatorLayout snackBarLayout) {
        Log.d("TESTING_URL", "" + Constant.BASE_URL);
        listOfPlans.clear();
        if (qrCode.equals("nothing")) {
            //listOfPlans = databaseAdapter.getPlansByReference(qrCode);
            listOfPlans = databaseAdapter.getPlans();
            getPLanInterface.gotPlan(listOfPlans);
            if (listOfPlans.size() > 0) {
                Snackbar.make(snackBarLayout, "Data showing from local storage", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(snackBarLayout, "No data found!", Snackbar.LENGTH_SHORT).show();
            }

        }
//        if (listOfPlans.size() > 0) {
//            getPLanInterface.gotPlan(listOfPlans);
//            Snackbar.make(snackBarLayout, "Data showing from local storage", Snackbar.LENGTH_SHORT).show();
//            //printToast(Thread.currentThread().getName());
//        }
        else {
            compositeDisposable.add(apis.getPlan(qrCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody responseBody) throws Throwable {
                            JSONArray root = new JSONArray(responseBody.string());
                            Log.d("RESPONSE", responseBody.string());
                            listOfPlans.clear();
                            for (int i = 0; i < root.length(); i++) {
                                JSONObject single = root.getJSONObject(i);
                                int intAutoPicking = single.getInt("intAutoPicking");
                                String Storename = single.getString("Storename");
                                String Quantity = single.getString("Quantity");
                                String ItemCode = single.getString("ItemCode");
                                String Description = single.getString("Description");
                                String SalesOrderNo = single.getString("SalesOrderNo");
                                int OrderId = single.getInt("OrderId");
                                String mass = single.getString("mass");
                                int LineNos = single.getInt("LineNos");
                                String weights = single.getString("weights");
                                String OrderDate = single.getString("OrderDate");
                                String Instruction = single.getString("Instruction");
                                String Area = single.getString("Area");
                                String Toinvoice = single.getString("Toinvoice");

                                PlanModel model = new PlanModel(intAutoPicking, Storename, Quantity, ItemCode, Description,
                                        SalesOrderNo, OrderId, mass, LineNos, weights, OrderDate, Instruction, Area, Toinvoice,
                                        "" + Toinvoice, 1, "" + Constant.BASE_URL, "" + qrCode);
//                                model.setReference(qrCode);
//                                model.setToLoad("0");
                                listOfPlans.add(model);
                            }
                            getPLanInterface.gotPlan(listOfPlans);
                            insertIntoSqliteDatabase(listOfPlans, qrCode, snackBarLayout);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Throwable {
                            getPLanInterface.error(throwable.getMessage());
                        }
                    }));
        }
    }

    public void getInvoice(GetPLanInterface getPLanInterface, String qrCode, ConstraintLayout snackBarLayout) {
        Log.d("TESTING_URL", "" + Constant.BASE_URL);
        listOfPlans.clear();
        compositeDisposable.add(apis.getPlan(qrCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Throwable {
                        JSONArray root = new JSONArray(responseBody.string());
                        Log.d("RESPONSE", responseBody.string());
                        listOfPlans.clear();
                        for (int i = 0; i < root.length(); i++) {
                            JSONObject single = root.getJSONObject(i);
                            int intAutoPicking = single.getInt("intAutoPicking");
                            String Storename = single.getString("Storename");
                            String Quantity = single.getString("Quantity");
                            String ItemCode = single.getString("ItemCode");
                            String Description = single.getString("Description");
                            String SalesOrderNo = single.getString("SalesOrderNo");
                            int OrderId = single.getInt("OrderId");
                            String mass = single.getString("mass");
                            int LineNos = single.getInt("LineNos");
                            String weights = single.getString("weights");
                            String OrderDate = single.getString("OrderDate");
                            String Instruction = single.getString("Instruction");
                            String Area = single.getString("Area");
                            String Toinvoice = single.getString("Toinvoice");

                            PlanModel model = new PlanModel(intAutoPicking, Storename, Quantity, ItemCode, Description,
                                    SalesOrderNo, OrderId, mass, LineNos, weights, OrderDate, Instruction, Area, Toinvoice,
                                    "" + Toinvoice, 1, "" + Constant.BASE_URL, "" + qrCode);
//                                model.setReference(qrCode);
//                                model.setToLoad("0");
                            listOfPlans.add(model);
                        }
                        getPLanInterface.gotPlan(listOfPlans);
                        //insertIntoSqliteDatabase(listOfPlans, qrCode, snackBarLayout);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        getPLanInterface.error(throwable.getMessage());
                    }
                }));
    }


    //Insert into SQLite database:
    private void insertIntoSqliteDatabase(List<PlanModel> listOfPlans, String reference, CoordinatorLayout snackBarLayout) {

        Observable<PlanModel> observable = Observable.fromIterable(listOfPlans)
                .subscribeOn(Schedulers.io());
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(Object o) {
                PlanModel model = (PlanModel) o;
                long id = databaseAdapter.insertPlans(model.getIntAutoPicking(), model.getStorename(), model.getQuantity(),
                        model.getItemCode(), model.getDescription(), model.getSalesOrderNo(), model.getOrderId(),
                        model.getMass(), model.getLineNos(), model.getWeights(), model.getOrderDate(), model.getInstruction(),
                        model.getArea(), model.getToinvoice(), model.getToLoad(), reference);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Snackbar.make(snackBarLayout, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                Snackbar.make(snackBarLayout, "Saved on local storage", Snackbar.LENGTH_SHORT).show();
            }
        };
        observable.subscribe(observer);
    }

    //Post on server database:
    // public void postDataOnServer(List<PostModel> listOfPostData, ConstraintLayout snackBarLayout, int userId) {
    public void postDataOnServer(List<PlanModel> listOfPostData, ConstraintLayout snackBarLayout, int userId) {

        //Toast.makeText(context, ""+userId, Toast.LENGTH_SHORT).show();

        Observable<PlanModel> observable = Observable.fromIterable(listOfPostData)
                .subscribeOn(Schedulers.io());
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                PlanModel model = (PlanModel) o;
                compositeDisposable.add(apis
                        .postPickedQty(model.getIntAutoPicking(),
                                model.getToLoad(),
                                userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Throwable {
                                //Log.d("UPLOAD_STATUS", responseBody.string());
                                //JSONArray data = new JSONArray(responseBody.string());
                                try {
                                    JSONArray root = new JSONArray(responseBody.string());
                                    if (root.length() > 0) {
                                        for (int i = 0; i < root.length(); i++) {
                                            JSONObject single = root.getJSONObject(i);
                                            String toLoad = single.getString("toLoad");
                                            String result = single.getString("result");
                                            //Snackbar.make(snackBarLayout, "Data is uploading (" + result + ")", Snackbar.LENGTH_SHORT).show();
                                            Log.d("UPLOAD_STATUS", "toLoad: " + toLoad + " InModel: " + model.getToLoad());
                                            databaseAdapter.updatePlanByLine(model.getLineNos(), 0);
                                        }
                                    }

                                } catch (Exception e) {
                                    Snackbar.make(snackBarLayout, "ERROR: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    Log.d("UPLOAD_STATUS", "ERROR: " + e.getMessage());
                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Throwable {
                                Log.d("ERROR_TESTING", throwable.getMessage());
                                Snackbar.make(snackBarLayout, "Error: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Snackbar.make(snackBarLayout, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        context.startActivity(new Intent(context, BarcodeActivity.class)
//                                //.putExtra("qrCode", "nothing")
//                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        context.startActivity(new Intent(context, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(context, "Data posted successfully", Toast.LENGTH_SHORT).show();
                        databaseAdapter.dropPlanTable();
                        Snackbar.make(snackBarLayout, "Data posted successfully", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        };
        observable.subscribe(observer);
    }


    private void insertRef(List<RefModel> list, int id) {
        Observable<RefModel> observable = Observable.fromIterable(list)
                .subscribeOn(Schedulers.io());

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                RefModel model = (RefModel) o;
                databaseAdapter.insertRef(model.getIntAutoPickingHeader(), model.getStrUnickReference(), model.getStrPickingNickname(),
                        id);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Saved Locally", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        observable.subscribe(observer);
    }
}
