package com.twilio.twiliochat.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.parse.ParseObject;
import com.twilio.twiliochat.R;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout fullNameLayout;
    private LinearLayout emailLayout;
    private Button createAccountButton;
    private Button loginButton;

    private Boolean isSigningUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fullNameLayout = (LinearLayout) findViewById(R.id.layoutFullName);
        emailLayout = (LinearLayout) findViewById(R.id.layoutEmail);
        createAccountButton = (Button) findViewById(R.id.buttonCreateAccount);
        loginButton = (Button) findViewById(R.id.buttonLogin);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleIsSigningUp();
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
}
