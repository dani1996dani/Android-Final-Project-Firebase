package com.tudu.tudu.Credentials;

import android.support.v7.app.AlertDialog;

import org.json.JSONObject;

public interface LoginListener {
    void onSuccessfulLogin(AlertDialog loadingDialog);
    void onFailedLogin(AlertDialog loadingDialog);

}
