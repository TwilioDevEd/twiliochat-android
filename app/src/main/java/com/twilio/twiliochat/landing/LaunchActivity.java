package com.twilio.twiliochat.landing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twilio.twiliochat.application.SessionManager;
import com.twilio.twiliochat.chat.MainChatActivity;

public class LaunchActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent launchIntent = new Intent();
    Class<?> launchActivity;

    launchActivity = getLaunchClass();
    launchIntent.setClass(getApplicationContext(), launchActivity);
    startActivity(launchIntent);

    finish();
  }

  private Class<?> getLaunchClass() {
    if (SessionManager.getInstance().isLoggedIn()) {
      return MainChatActivity.class;
    } else {
      return LoginActivity.class;
    }
  }
}
