package com.tudu.tudu;

import android.app.Activity;
import android.content.Intent;

public class Consts {

    public static final String INTENT_FRAGMENT_TYPE = "fragment_type";
    public static final String PREFS = "prefs";
    public static final String IMPORTANT_COLOR_ID = "important_color_id";

    /**
     * Minimize the current app, and go to the home screen of the phone.
     * @param activity The activity your are leaving.
     */
    public static void defaultBackPressed(Activity activity){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
    }

}
