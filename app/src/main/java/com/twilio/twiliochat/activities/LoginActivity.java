package com.twilio.twiliochat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.util.AlertDialogHandler;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout fullNameLayout;
    private LinearLayout emailLayout;
    private LinearLayout formLayout;
    private Button createAccountButton;
    private Button loginButton;
    private Button forgotPasswordButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;

    ProgressDialog progressDialog;
    final Context context = this;

    private Boolean isSigningUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fullNameLayout = (LinearLayout) findViewById(R.id.layoutFullName);
        emailLayout = (LinearLayout) findViewById(R.id.layoutEmail);
        formLayout = (LinearLayout) findViewById(R.id.linearLayoutTextFields);
        createAccountButton = (Button) findViewById(R.id.buttonCreateAccount);
        loginButton = (Button) findViewById(R.id.buttonLogin);
        forgotPasswordButton = (Button) findViewById(R.id.buttonForgotPassword);
        usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        fullNameEditText = (EditText) findViewById(R.id.editTextFullName);
        emailEditText = (EditText) findViewById(R.id.editTextEmail);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleIsSigningUp();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSigningUp) {
                    return;
                }
                login();
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void toggleIsSigningUp() {
        Resources resources = getResources();
        int createAccountStringId;
        int loginStringId;
        int desiredVisibility;
        isSigningUp = !isSigningUp;

        if (isSigningUp) {
            createAccountStringId = R.string.back_to_login_button_text;
            loginStringId = R.string.register_button_text;
            desiredVisibility = View.VISIBLE;

        }
        else {
            createAccountStringId = R.string.create_account_button_text;
            loginStringId = R.string.login_button_text;
            desiredVisibility = View.GONE;
        }
        fullNameLayout.setVisibility(desiredVisibility);
        emailLayout.setVisibility(desiredVisibility);
        createAccountButton.setText(resources.getText(createAccountStringId));
        loginButton.setText(loginStringId);
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        startBackgroundActionWithMessage("signing in...");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showMainChatActivity();
                } else {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    AlertDialogHandler.displayAlertWithMessage(e.getLocalizedMessage(), context);
                    setFormEnabled(true);
                }
            }
        });
    }

    private void startBackgroundActionWithMessage(String message) {
        setFormEnabled(false);
        showActivityIndicator(message);
    }

    private void setFormEnabled(Boolean enabled) {
        usernameEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        fullNameEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
    }

    private void showActivityIndicator(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private void showMainChatActivity() {
        Intent launchIntent = new Intent();
        launchIntent.setClass(getApplicationContext(), MainChatActivity.class);
        startActivity(launchIntent);

        finish();
    }
}
