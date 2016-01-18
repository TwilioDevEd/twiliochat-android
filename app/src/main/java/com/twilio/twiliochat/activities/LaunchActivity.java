package com.twilio.twiliochat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

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
    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      return MainChatActivity.class;
    } else {
      return LoginActivity.class;
    }
  }
}
