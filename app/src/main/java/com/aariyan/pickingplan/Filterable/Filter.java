package com.aariyan.pickingplan.Filterable;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Model.PostModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Filter {

    private Context context;

    public Filter(Context context) {
        this.context = context;
    }

    public List<PlanModel> getFilteredData(List<PlanModel> listOfPlans, int flag) {
        List<PlanModel> list = new ArrayList<>();
        //Observable<PlanModel> observable = Observable.fromIterable(listOfPlans).filter(planModel -> planModel.getToLoad().equals("0") || TextUtils.isEmpty(planModel.getToLoad()));
        Observable<PlanModel> observable = Observable.
                fromIterable(listOfPlans)
                .filter(new Predicate<PlanModel>() {
                    @Override
                    public boolean test(PlanModel planModel) throws Throwable {
                        if (flag == 1) {
                            //Remaining
                            return planModel.getToLoad().equals("0");
                        } else {
                            //Loaded
                            //return Integer.parseInt(planModel.getToLoad()) > 0;
                            return !planModel.getToLoad().equals("0");
                        }
                    }
                });

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                list.add((PlanModel) o);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);
        return list;
    }

    public List<PlanModel> getPostData(List<PlanModel> listOfPlans) {
        List<PlanModel> list = new ArrayList<>();
//        Observable<PlanModel> observable = Observable.fromIterable(listOfPlans)
//                .filter(planModel -> Integer.parseInt(planModel.getToLoad()) > 0 || !TextUtils.isEmpty(planModel.getToLoad()))
//                .subscribeOn(Schedulers.io());

        Observable<PlanModel> observable = Observable.fromIterable(listOfPlans)
                .filter(planModel -> planModel.getFlag() != 0)
                .subscribeOn(Schedulers.io());


        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                PlanModel model = (PlanModel) o;
//                Log.d("QUANTITY_TESTING", "onNext: " + model.getQuantity());
//                PostModel postModel = new PostModel(
//                        model.getIntAutoPicking(), model.getDescription(), model.getToLoad(), model.getLineNos()
//                );

                list.add(model);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);
        return list;
    }
}
