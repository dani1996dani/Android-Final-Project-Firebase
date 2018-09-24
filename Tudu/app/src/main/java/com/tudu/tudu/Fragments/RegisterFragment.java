package com.tudu.tudu.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.FirebaseDatabase;

import com.tudu.tudu.AlertDialogManager;
import com.tudu.tudu.Interfaces.AlertListener;
import com.tudu.tudu.Credentials.CredentialsManager;
import com.tudu.tudu.R;
import com.tudu.tudu.Credentials.LoginListener;
import com.tudu.tudu.User;


public class RegisterFragment extends Fragment {


    EditText inputEmail, inputPassword;
    String email, password;
    View fragmentView;
    TextView textError;
    LinearLayout swapToLoginLayout;
    LoginFragment loginFragment;
    LoginListener loginListener;
    AlertListener alertListener;
    FirebaseAuth firebaseAuth;
    Context context;

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public void setAlertListener(AlertListener listener) {
        this.alertListener = listener;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentView == null)
            fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        context = fragmentView.getContext();

        Button actionBtn = fragmentView.findViewById(R.id.btn_login);
        inputEmail = fragmentView.findViewById(R.id.input_username);
        inputPassword = fragmentView.findViewById(R.id.input_password);
        textError = fragmentView.findViewById(R.id.textError);
        swapToLoginLayout = fragmentView.findViewById(R.id.swapLoginLayout);

        //Set onclicklistener for the register button.
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();

                register(email, password);
            }
        });

        //clicking on the swapToSignupLayout will load up the login fragment on screen.
        swapToLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginFragment == null) {
                    loginFragment = new LoginFragment();
                    loginFragment.setLoginListener(loginListener);
                    loginFragment.setAlertListener(alertListener);
                    loginFragment.setFirebaseAuth(firebaseAuth);
                }

                getFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.loginFragmentContainer, loginFragment).commit();
            }
        });


        return fragmentView;
    }

    /**
     * First, checks if the email and password are considered valid. If they are, continues and tries to register. If at some point the procedure fails, an error messaage will be displayed on screen.
     * @param email The email to register.
     * @param password The password to register.
     */
    public void register(final String email, final String password) {

        String possibleError = "";

        CredentialsManager credentialsManager = CredentialsManager.getCredentialsManager();
        if (!credentialsManager.isValidEmail(email)) {
            possibleError = CredentialsManager.INVALID_EMAIL_MESSAGE;
            alertListener.toggleAlertMessage(textError, true, possibleError);
            return;
        }
        if (!credentialsManager.isValidPassword(password)) {
            possibleError = CredentialsManager.INVALID_PASSWORD_MESSAGE;
            alertListener.toggleAlertMessage(textError, true, possibleError);

            return;
        }

        //If everything is ok, hide the error TextView if it is showing.
        alertListener.toggleAlertMessage(textError, false, possibleError);

        //Showing a loading dialog.
        final AlertDialog loadingDialog = AlertDialogManager.getAlertDialogManager().getLoadingAlertDialog(context,"Please Wait");
        loadingDialog.show();


        //if we made it this far, the email and password are up to our standards
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Add the newly registered user to our database and handle the successful login.
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(new User(email, password, uid));
                    loginListener.onSuccessfulLogin(loadingDialog);
                } else {
                    //Handle unsuccessful registration.
                    loginListener.onFailedLogin(loadingDialog);
                    alertListener.toggleAlertMessage(textError, true, task.getException().getMessage());
                }
            }
        });
    }


}
