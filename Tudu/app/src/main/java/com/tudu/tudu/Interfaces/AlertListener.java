package com.tudu.tudu.Interfaces;


import android.widget.TextView;

public interface AlertListener {
    void toggleAlertMessage(TextView textError, boolean shouldShowError, String messageToDisplay);
}
