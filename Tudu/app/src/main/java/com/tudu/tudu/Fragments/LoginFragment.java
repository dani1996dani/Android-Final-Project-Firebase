package com.tudu.tudu.Fragments;

//import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.tudu.tudu.AlertDialogManager;
import com.tudu.tudu.Interfaces.AlertListener;
import com.tudu.tudu.Credentials.CredentialsManager;
import com.tudu.tudu.R;
import com.tudu.tudu.Credentials.LoginListener;
import android.support.v7.app.AlertDialog;


public class LoginFragment extends Fragment {

    EditText inputEmail, inputPassword;
    String email, password;
    View fragmentView;
    Context context;
    TextView textError;
    LinearLayout swapToSignupLayout;
    RegisterFragment registerFragment;
    LoginListener loginListener;
    AlertListener alertListener;
    FirebaseAuth firebaseAuth;



    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setAlertListener(AlertListener listener) {
        this.alertListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentView == null)
            fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        context = fragmentView.getContext();
        Button actionBtn = fragmentView.findViewById(R.id.btn_login);
        inputEmail = fragmentView.findViewById(R.id.input_username);
        inputPassword = fragmentView.findViewById(R.id.input_password);
        textError = fragmentView.findViewById(R.id.textError);
        swapToSignupLayout = fragmentView.findViewById(R.id.swapSignupLayout);


        //setting onclicklistener for the login button.
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                login(email, password);
            }
        });

        //clicking on the swapToSignupLayout will load up the register fragment on screen.
        swapToSignupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registerFragment == null) {
                    registerFragment = new RegisterFragment();
                    registerFragment.setLoginListener(loginListener);
                    registerFragment.setAlertListener(alertListener);
                    registerFragment.setFirebaseAuth(firebaseAuth);
                }

                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.loginFragmentContainer, registerFragment).commit();
            }
        });


        return fragmentView;
    }

    /**
     * First, checks if the email and password are considered valid. If they are, continues and tries to log in. If at some point the procedure fails, an error messaage will be displayed on screen.
     * @param email The email to log in with.
     * @param password The password to log in with.
     */
    public void login(final String email, String password) {

        String possibleError = "";

        CredentialsManager credentialsManager = CredentialsManager.getCredentialsManager();
        //In case the email is invalid.
        if (!credentialsManager.isValidEmail(email)) {
            possibleError = CredentialsManager.INVALID_EMAIL_MESSAGE;
            alertListener.toggleAlertMessage(textError,true, possibleError);
            return;
        }
        //In case the password is invalid.
        if (!credentialsManager.isValidPassword(password)) {
            possibleError = CredentialsManager.INVALID_PASSWORD_MESSAGE;
            alertListener.toggleAlertMessage(textError,true, possibleError);
            return;
        }

        //If everything is ok, hide the error TextView if it is showing.
        alertListener.toggleAlertMessage(textError,false, possibleError);

        //Showing a loading dialog.
        final AlertDialog loadingDialog = AlertDialogManager.getAlertDialogManager().getLoadingAlertDialog(context,"Please Wait");
        loadingDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Handle successful login.
                    loginListener.onSuccessfulLogin(loadingDialog);
                }
                else {
                    //Handle failed login.
                    loginListener.onFailedLogin(loadingDialog);
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        alertListener.toggleAlertMessage(textError,true, CredentialsManager.INVALID_CREDENTIALS_MESSAGE);
                    } catch (Exception e) {
                        alertListener.toggleAlertMessage(textError,true, task.getException().getMessage());
                    }
                }
            }
        });
    }


}
