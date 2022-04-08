package com.aariyan.pickingplan.Interface;

import com.aariyan.pickingplan.Model.AuthenticationModel;

import java.util.List;

public interface LogInInterface {

    void checkLogIn(List<AuthenticationModel> list);
    void onError(String errorMessage);
}
