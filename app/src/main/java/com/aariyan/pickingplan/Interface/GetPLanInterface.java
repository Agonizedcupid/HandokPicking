package com.aariyan.pickingplan.Interface;

import com.aariyan.pickingplan.Model.PlanModel;

import java.util.List;

public interface GetPLanInterface {

    void gotPlan(List<PlanModel> listOfPlan);
    void error(String errorMessage);
}
