package com.twilio.twiliochat.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
  public static final String KEY_USERNAME = "username";
  private static final String PREF_NAME = "TWILIOCHAT";
  private static final String IS_LOGGED_IN = "IsLoggedIn";
  private static SessionManager instance =
      new SessionManager(TwilioChatApplication.get().getApplicationContext());
  SharedPreferences pref;
  Editor editor;
  Context context;
  int PRIVATE_MODE = 0;

  private SessionManager(Context context) {
    this.context = context;
    pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static SessionManager getInstance() {
    return instance;
  }

  public void createLoginSession(String username) {
    editor.putBoolean(IS_LOGGED_IN, true);
    editor.putString(KEY_USERNAME, username);
    // commit changes
    editor.commit();
  }

  public HashMap<String, String> getUserDetails() {
    HashMap<String, String> user = new HashMap<String, String>();
    user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

    return user;
  }

  public String getUsername() {
    return pref.getString(KEY_USERNAME, null);
  }

  public void logoutUser() {
    editor = editor.clear();
    editor.commit();
  }

  public boolean isLoggedIn() {
    return pref.getBoolean(IS_LOGGED_IN, false);
  }

}

