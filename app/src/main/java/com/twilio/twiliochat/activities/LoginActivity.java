package com.twilio.twiliochat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.ipmessaging.IPMessagingClient;
import com.twilio.twiliochat.ipmessaging.LoginListener;
import com.twilio.twiliochat.util.AlertDialogHandler;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
  final Context context = this;
  private final String USERNAME_FORM_FIELD = "username";
  private final String PASSWORD_FORM_FIELD = "password";
  private final String FULLNAME_FORM_FIELD = "fullName";
  private final String EMAIL_FORM_FIELD = "email";
  private ProgressDialog progressDialog;
  private LinearLayout fullNameLayout;
  private LinearLayout emailLayout;
  private Button createAccountButton;
  private Button loginButton;
  private Button forgotPasswordButton;
  private EditText usernameEditText;
  private EditText passwordEditText;
  private EditText fullNameEditText;
  private EditText emailEditText;
  private Boolean isSigningUp = false;

  private IPMessagingClient messagingClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setUIComponents();

    messagingClient = TwilioChatApplication.get().getIPMessagingClient();

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
          register();
          return;
        }
        login();
      }
    });

    forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showForgotPasswordActivity();
      }
    });
  }

  private void setUIComponents() {
    fullNameLayout = (LinearLayout) findViewById(R.id.layoutFullName);
    emailLayout = (LinearLayout) findViewById(R.id.layoutEmail);
    createAccountButton = (Button) findViewById(R.id.buttonCreateAccount);
    loginButton = (Button) findViewById(R.id.buttonLogin);
    forgotPasswordButton = (Button) findViewById(R.id.buttonForgotPassword);
    usernameEditText = (EditText) findViewById(R.id.editTextUsername);
    passwordEditText = (EditText) findViewById(R.id.editTextPassword);
    fullNameEditText = (EditText) findViewById(R.id.editTextFullName);
    emailEditText = (EditText) findViewById(R.id.editTextEmail);
  }

  private void toggleIsSigningUp() {
    int createAccountStringId;
    int loginStringId;
    int desiredVisibility;
    isSigningUp = !isSigningUp;

    if (isSigningUp) {
      createAccountStringId = R.string.back_to_login_button_text;
      loginStringId = R.string.register_button_text;
      desiredVisibility = View.VISIBLE;

    } else {
      createAccountStringId = R.string.create_account_button_text;
      loginStringId = R.string.login_button_text;
      desiredVisibility = View.GONE;
    }
    fullNameLayout.setVisibility(desiredVisibility);
    emailLayout.setVisibility(desiredVisibility);
    createAccountButton.setText(getStringResource(createAccountStringId));
    loginButton.setText(getStringResource(loginStringId));
  }

  private void register() {
    Map<String, String> formInput = getFormInput();
    if (formInput.size() < 4) {
      displayAllFieldsRequiredMessage();
      return;
    }
    startStatusDialogWithMessage(getStringResource(R.string.register_user_progress_message));
    ParseUser user = new ParseUser();
    user.setUsername(formInput.get(USERNAME_FORM_FIELD));
    user.setPassword(formInput.get(PASSWORD_FORM_FIELD));
    user.setEmail(formInput.get(EMAIL_FORM_FIELD));
    user.put(FULLNAME_FORM_FIELD, formInput.get(FULLNAME_FORM_FIELD));

    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
        stopStatusDialog();
        if (e != null) {
          showAlertWithMessage(e.getLocalizedMessage());
          return;
        }
        showMainChatActivity();
      }
    });
  }

  private void login() {
    Map<String, String> formInput = getFormInput();
    if (formInput.size() < 2) {
      displayAllFieldsRequiredMessage();
      return;
    }

    startStatusDialogWithMessage(getStringResource(R.string.login_user_progress_message));
    ParseUser.logInInBackground(formInput.get(USERNAME_FORM_FIELD), formInput.get(PASSWORD_FORM_FIELD), new LogInCallback() {
      public void done(ParseUser user, ParseException e) {
        if (user != null) {
          initializeMessagingClient();
        }
        else {
          stopStatusDialog();
          showAlertWithMessage(e.getLocalizedMessage());
        }
      }
    });
  }

  private Map<String, String> getFormInput() {
    String username = usernameEditText.getText().toString();
    String password = passwordEditText.getText().toString();
    String fullName = fullNameEditText.getText().toString();
    String email = emailEditText.getText().toString();

    Map<String, String> formInput = new HashMap<>();

    if (username.length() > 0) {
      formInput.put(USERNAME_FORM_FIELD, username);
    }
    if (password.length() > 0) {
      formInput.put(PASSWORD_FORM_FIELD, password);
    }

    if (isSigningUp) {
      if (fullName.length() > 0) {
        formInput.put(FULLNAME_FORM_FIELD, fullName);
      }
      if (email.length() > 0) {
        formInput.put(EMAIL_FORM_FIELD, email);
      }
    }
    return formInput;
  }

  private void initializeMessagingClient() {
    messagingClient.connectClient(new LoginListener() {
      @Override
      public void onLoginStarted() {

      }

      @Override
      public void onLoginFinished() {
        System.out.println("Client Connected");
        showMainChatActivity();
      }

      @Override
      public void onLoginError(String errorMessage) {
        stopStatusDialog();
        showAlertWithMessage(errorMessage);
      }

      @Override
      public void onLogoutFinished() {

      }
    });
  }

  private void displayAllFieldsRequiredMessage() {
    String message = getStringResource(R.string.login_all_fields_required);
    showAlertWithMessage(message);
  }

  private void startStatusDialogWithMessage(String message) {
    setFormEnabled(false);
    showActivityIndicator(message);
  }

  private void stopStatusDialog() {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    setFormEnabled(true);
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

  private void showForgotPasswordActivity() {
    Intent launchIntent = new Intent();
    launchIntent.setClass(getApplicationContext(), ForgotPasswordActivity.class);
    startActivity(launchIntent);
  }

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void showAlertWithMessage(String message) {
    AlertDialogHandler.displayAlertWithMessage(message, context);
  }

  private Map<String, String> getTokenRequestParams() {
    String android_id = Settings.Secure.getString(context.getContentResolver(),
        Settings.Secure.ANDROID_ID);
    Map<String, String> params = new HashMap<>();
    params.put("device", android_id);

    return params;
  }

  private String getAccessToken() {
    Map<String, String> params = new HashMap<>();
    return "";
  }
}
