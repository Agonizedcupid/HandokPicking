package com.aariyan.pickingplan.Interface;

import com.aariyan.pickingplan.Model.RefModel;

import java.util.List;

public interface RefInterface {
    void onSuccess(List<RefModel> list);
    void onError(String message);
}
