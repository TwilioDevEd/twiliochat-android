package com.twilio.twiliochat.application;

import android.app.Application;

import com.twilio.twiliochat.chat.ChatClientManager;

public class TwilioChatApplication extends Application {
  private static TwilioChatApplication instance;
  private ChatClientManager basicClient;

  public static TwilioChatApplication get() {
    return instance;
  }

  public ChatClientManager getChatClientManager() {
    return this.basicClient;
  }

  public static final String TAG = "TwilioChat";

  @Override
  public void onCreate() {
    super.onCreate();

    TwilioChatApplication.instance = this;
    basicClient = new ChatClientManager(getApplicationContext());
  }
}
