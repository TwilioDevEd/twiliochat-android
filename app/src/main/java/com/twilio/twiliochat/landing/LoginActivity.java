package com.twilio.twiliochat.landing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.AlertDialogHandler;
import com.twilio.twiliochat.application.SessionManager;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.chat.ChatClientManager;
import com.twilio.twiliochat.chat.MainChatActivity;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
  final Context context = this;
  private final String USERNAME_FORM_FIELD = "username";
  private ProgressDialog progressDialog;
  private Button loginButton;
  private EditText usernameEditText;

  private ChatClientManager clientManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    setUIComponents();

    clientManager = TwilioChatApplication.get().getChatClientManager();
  }

  private void setUIComponents() {
    loginButton = (Button) findViewById(R.id.buttonLogin);
    usernameEditText = (EditText) findViewById(R.id.editTextUsername);

    setUpListeners();
  }

  private void setUpListeners() {
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        login();
      }
    });
    TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int viewId = v.getImeActionId();
        if (viewId == 100) {
          login();
          return true;
        }
        return false;
      }
    };
    usernameEditText.setOnEditorActionListener(actionListener);
  }

  private void login() {
    Map<String, String> formInput = getFormInput();
    if (formInput.size() < 1) {
      displayAllFieldsRequiredMessage();
      return;
    }

    startStatusDialogWithMessage(getStringResource(R.string.login_user_progress_message));
    SessionManager.getInstance().createLoginSession(formInput.get(USERNAME_FORM_FIELD));
    initializeChatClient();
  }

  private Map<String, String> getFormInput() {
    String username = usernameEditText.getText().toString();

    Map<String, String> formInput = new HashMap<>();

    if (username.length() > 0) {
      formInput.put(USERNAME_FORM_FIELD, username);
    }
    return formInput;
  }

  private void initializeChatClient() {
    clientManager.connectClient(new TaskCompletionListener<Void, String>() {
      @Override
      public void onSuccess(Void aVoid) {
        showMainChatActivity();
      }

      @Override
      public void onError(String errorMessage) {
        stopStatusDialog();
        showAlertWithMessage(errorMessage);
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

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void showAlertWithMessage(String message) {
    AlertDialogHandler.displayAlertWithMessage(message, context);
  }
}
