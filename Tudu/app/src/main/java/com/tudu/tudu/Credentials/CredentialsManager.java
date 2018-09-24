package com.tudu.tudu.Credentials;


import java.util.regex.Pattern;

public class CredentialsManager {

    private static CredentialsManager credentialsManager;

    public static final String INVALID_EMAIL_MESSAGE = "Invalid Email";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid Credentials";
    public static final String INVALID_PASSWORD_MESSAGE = "Password must be between 6 and 16 characters long and include at least one numeric digit.";

    public static CredentialsManager getCredentialsManager() {
        if (credentialsManager == null)
            credentialsManager = new CredentialsManager();
        return credentialsManager;
    }

    /**
     *
     * @param email The email to check.
     * @return True if the email is considered a valid email, false otherwise.
     */
    public boolean isValidEmail(String email) {
        boolean result = false;

        if (email == null || email.isEmpty() || email.length() > 32)
            return result;
        //"^[a-z0-9]+((\\.)?[a-zA-Z0-9-])*@([a-zA-Z0-9]+\\.)+[a-z]+$"
        String emailRegex = "^[a-z0-9]+((\\.)?[a-zA-Z0-9-])*@([a-zA-Z0-9]+\\.)+[a-z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        result = pattern.matcher(email).matches();
        return result;
    }

    /**
     *
     * @param password The password to check.
     * @return True if the password is considered a valid password, false otherwise.
     */
    public boolean isValidPassword(String password) {
        boolean result = false;

        if (password == null || password.isEmpty() || password.length() > 32)
            return result;

        String passwordRegex = "^(?=.*\\d).{6,16}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        result = pattern.matcher(password).matches();

        return result;
    }
}