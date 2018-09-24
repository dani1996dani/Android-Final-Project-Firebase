package com.tudu.tudu;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tudu.tudu.Enums.ImportantColor;
import com.tudu.tudu.Interfaces.SettingsListener;

import java.util.List;

public class AlertDialogManager {

    private static AlertDialogManager alertDialogManager;

    private AlertDialogManager() {

    }

    public static AlertDialogManager getAlertDialogManager() {
        if (alertDialogManager == null)
            alertDialogManager = new AlertDialogManager();
        return alertDialogManager;
    }

    /**
     * Creates a new add task alert dialog.
     *
     * @param context          The context to be displayed in.
     * @param firebaseDatabase the firebaseDatabase to add the new task to.
     * @return a new add task alert dialog.
     */
    public AlertDialog getAddTaskAlertDialog(Context context, final FirebaseDatabase firebaseDatabase) {

        //Prepare a custom view for the alert dialog.
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.alert_task_input, null, false);
        final EditText editTextNewTitle = inflatedView.findViewById(R.id.txtNewTitle);
        final EditText editTextNewContent = inflatedView.findViewById(R.id.txtNewContent);
        final CheckBox importance = inflatedView.findViewById(R.id.checkboxImportance);
        Button btnOk = inflatedView.findViewById(R.id.btnOkDialog);

        final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(inflatedView).create();

        //What should happen when the OK button was clicked. Adding a new Task to the Tasks node in the database, and then, hiding the alert dialog.
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = firebaseDatabase.getReference("Tasks");
                String key = reference.push().getKey();
                reference.child(key).setValue(new Task(editTextNewTitle.getText().toString().trim(), editTextNewContent.getText().toString().trim(), key, FirebaseAuth.getInstance().getCurrentUser().getUid(), importance.isChecked() ? 1 : 0));

                alertDialog.dismiss();
            }
        });

        //Hiding the dialog when the cancel button is clicked.
        Button btnCancel = inflatedView.findViewById(R.id.btnCancelDialog);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    /**
     * Creates a new edit task alert dialog.
     *
     * @param context          The context to be displayed in.
     * @param currentTask      The task object that is being modifed.
     * @param firebaseDatabase The firebaseDatabase to add the new task to.
     * @return a new edit task alert dialog.
     */
    public AlertDialog getEditTaskAlertDialog(Context context, final Task currentTask, final FirebaseDatabase firebaseDatabase) {

        //Prepare a custom view for the alert dialog.
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.alert_task_editor, null, false);

        final EditText currentTitle = inflatedView.findViewById(R.id.edittextCurrentTitle);
        final EditText currentContent = inflatedView.findViewById(R.id.edittextCurrentContent);
        final CheckBox importance = inflatedView.findViewById(R.id.checkboxImportance);

        //setting the title and content EditTexts text to the current Task text.
        currentTitle.setText(currentTask.getTitle());
        currentContent.setText(currentTask.getContent());
        importance.setChecked(currentTask.getImportance() == 1 ? true : false);

        //finding the edit and delete buttons on the custom view.
        Button editButton = inflatedView.findViewById(R.id.btnEditDoneDialog);
        Button deleteButton = inflatedView.findViewById(R.id.btnDeleteTaskDialog);

        final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(inflatedView).create();

        //When the Edit button is clicked, change the currentTask object to the new values, overwrite those values in the database, and hide the dialog.
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTask.setTitle(currentTitle.getText().toString().trim());
                currentTask.setContent(currentContent.getText().toString().trim());
                currentTask.setImportance(importance.isChecked() ? 1 : 0);

                DatabaseReference reference = firebaseDatabase.getReference("Tasks/" + currentTask.getTaskUid());
                reference.setValue(currentTask);
                alertDialog.dismiss();
            }
        });

        //When the Delete button is clicked, delete the task from the database and hide the dialog.
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = firebaseDatabase.getReference("Tasks");
                reference.child(currentTask.getTaskUid()).removeValue();
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    /**
     * Creates a new Settings alert dialog.
     *
     * @param context          The context to be displayed in.
     * @param settingsListener a SettingsListener to be notifed when the settings has changed.
     * @return a new Settings alert dialog.
     */
    public AlertDialog getSettingsAlertDialog(final Context context, final SettingsListener settingsListener) {

        //Prepare a custom view for the alert dialog.
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.dialog_settings, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(context).setView(inflatedView).create();
        final RadioGroup radioGroupColors = inflatedView.findViewById(R.id.radioGroup_Colors);
        Button btnOk = inflatedView.findViewById(R.id.btnOkDialog);
        Button btnCancel = inflatedView.findViewById(R.id.btnCancelDialog);


        //getting the checked radio button id, and set the sharedPrefs to contain the new color selected.
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.PREFS, Context.MODE_PRIVATE);
                int radioButtonId = radioGroupColors.getCheckedRadioButtonId();

                //yellow is the default color, and it will be checked first.
                String color = "yellow";
                ImportantColor importantColor = ImportantColor.Yellow;
                switch (radioButtonId) {
                    case R.id.yellowColor:
                        color = "yellow";
                        importantColor = ImportantColor.Yellow;
                        break;

                    case R.id.greenColor:
                        color = "green";
                        importantColor = ImportantColor.Green;
                        break;

                    case R.id.redColor:
                        color = "red";
                        importantColor = ImportantColor.Red;
                        break;
                    default:
                        break;
                }

                settingsListener.onImportantColorChanged(importantColor);

                sharedPreferences.edit().putString(Consts.IMPORTANT_COLOR_ID, color).commit();
                alertDialog.dismiss();
            }
        });

        //Hide the dialog.
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        return alertDialog;
    }

    /**
     * Creates a new Loading alert dialog.
     *
     * @param context The context to be displayed in.
     * @param message The message to display while the user waits.
     * @return a new Loading alert dialog.
     */
    public AlertDialog getLoadingAlertDialog(Context context, String message) {

        //Prepare a custom view for the alert dialog, make it not cancelable so the user wont canel the loading screen by accident.
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null, false);
        TextView textLoading = inflatedView.findViewById(R.id.txtLoading);
        textLoading.setText(message);
        alertDialog.setView(inflatedView);

        return alertDialog;
    }
}
