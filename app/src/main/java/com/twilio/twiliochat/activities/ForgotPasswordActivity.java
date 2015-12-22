package com.twilio.twiliochat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.util.AlertDialogHandler;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
  private final String EMAIL_FORM_FIELD = "email";
  private final Context context = this;
  private ProgressDialog progressDialog;
  private Button goBackButton;
  private Button sendButton;
  private EditText emailEditText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    setUIComponents();

    goBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        backToLogin();
      }
    });

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendPasswordRecoveryEmail();
      }
    });
  }

  private void setUIComponents() {
    goBackButton = (Button) findViewById(R.id.buttonGoBack);
    sendButton = (Button) findViewById(R.id.buttonSend);
    emailEditText = (EditText) findViewById(R.id.editTextEmail);
  }

  private void sendPasswordRecoveryEmail() {
    Map<String, String> formInput = getFormInput();
    if (formInput.size() < 1) {
      displayEmailFieldRequiredMessage();
      return;
    }
    startStatusDialogWithMessage(getStringResource(R.string.forgot_password_progress_message));
    ParseUser.requestPasswordResetInBackground(formInput.get(EMAIL_FORM_FIELD), new RequestPasswordResetCallback() {
      public void done(ParseException e) {
        stopStatusDialog();
        if (e == null) {
          AlertDialogHandler.displayAlertWithHandler("success", context, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          });
          return;
        } else {
          showAlertWithMessage(e.getLocalizedMessage());
        }
      }
    });
  }

  private Map<String, String> getFormInput() {
    String email = emailEditText.getText().toString();

    Map<String, String> formInput = new HashMap<>();

    if (email.length() > 0) {
      formInput.put(EMAIL_FORM_FIELD, email);
    }

    return formInput;
  }

  private void backToLogin() {
    finish();
  }

  private void displayEmailFieldRequiredMessage() {
    String message = getStringResource(R.string.forgot_password_email_required);
    AlertDialogHandler.displayAlertWithMessage(message, context);
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
    emailEditText.setEnabled(enabled);
  }

  private void showActivityIndicator(String message) {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage(message);
    progressDialog.show();
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
  }

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void showAlertWithMessage(String message) {
    AlertDialogHandler.displayAlertWithMessage(message, context);
  }
}
