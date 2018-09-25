package com.tudu.tudu.Activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tudu.tudu.Interfaces.AlertListener;
import com.tudu.tudu.Consts;
import com.tudu.tudu.Fragments.LoginFragment;
import com.tudu.tudu.Fragments.RegisterFragment;
import com.tudu.tudu.Credentials.LoginListener;
import com.tudu.tudu.R;

public class LoginActivity extends AppCompatActivity implements AlertListener, LoginListener {


    EditText email, password;
    FirebaseAuth firebaseAuth;
    FrameLayout loginFragmentContainer;
    boolean isShowingErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if user is already logged in, send him to the TasksActivity.
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            onSuccessfulLogin();
            return;
        }

        //Loads up the login/register screen in case the user is not logged in.
        email = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);

        loginFragmentContainer = findViewById(R.id.loginFragmentContainer);

        //getting the fragment type from the intent (either just entered the app or came from TasksActivity), if none is present, 0 is the default and the register screen will show up.
        int fragmentType = getIntent().getIntExtra(Consts.INTENT_FRAGMENT_TYPE,0);
        loadLoginFragment(fragmentType);
    }


    /**
     * Displays/Hides a TextView that shows an error message.
     * @param textError The TextView to display the error in.
     * @param shouldShowError True - Show error. False - Hide error.
     * @param messageToDisplay The message to display.
     */
    @Override
    public void toggleAlertMessage(TextView textError, boolean shouldShowError, String messageToDisplay) {

        if (shouldShowError) {
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);

            fadeIn.setDuration(300);
            fadeIn.setFillAfter(true);

            textError.startAnimation(fadeIn);
            textError.setText("\u26A0 " + messageToDisplay);
            isShowingErrorMessage = true;

            return;
        } else {

            if (isShowingErrorMessage) {
                AlphaAnimation fadeOut = new AlphaAnimation(textError.getAlpha(), 0.0f);

                fadeOut.setDuration(100);
                fadeOut.setFillAfter(true);

                textError.startAnimation(fadeOut);
                isShowingErrorMessage = false;
            }
        }
    }

    /**
     * Sends the user to TasksActivity after a successful login.
     * @param loadingDialog The loading dialog to hide.
     */
    @Override
    public void onSuccessfulLogin(AlertDialog loadingDialog) {
        loadingDialog.dismiss();
        Intent i = new Intent(this, TasksActivity.class);
        i.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
        startActivity(i);
        finish();
    }

    /**
     * Sends the user to TasksActivity after a successful login.
     */
    public void onSuccessfulLogin() {
        Intent i = new Intent(this, TasksActivity.class);
        i.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
        startActivity(i);
        finish();
    }

    /**
     * Simply hides the loading dialog on a failed login attempt.
     * @param loadingDialog The loading dialog to hide.
     */
    @Override
    public void onFailedLogin(AlertDialog loadingDialog) {
        loadingDialog.dismiss();
    }

    //Overriding the default back button behaviour with MY default back button behavior (doing this so onDestroy wont be called).
    @Override
    public void onBackPressed() {
        Consts.defaultBackPressed(this);
    }

    /**
     * Loads the desired fragment on to the screen.
     * @param fragmentType the type of fragment to load up on to the screen. 0 - register fragment. 1 - login fragment.
     */
    public void loadLoginFragment(int fragmentType){
        if(fragmentType == 0) {
            RegisterFragment registerFragment = new RegisterFragment();
            registerFragment.setFirebaseAuth(firebaseAuth);
            registerFragment.setLoginListener(this);
            registerFragment.setAlertListener(this);
            getFragmentManager().beginTransaction().replace(R.id.loginFragmentContainer, registerFragment).commit();
        }
        else if(fragmentType == 1){
            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setFirebaseAuth(firebaseAuth);
            loginFragment.setLoginListener(this);
            loginFragment.setAlertListener(this);
            getFragmentManager().beginTransaction().replace(R.id.loginFragmentContainer, loginFragment).commit();
        }
    }
}
