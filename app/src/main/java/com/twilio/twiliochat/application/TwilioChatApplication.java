package com.twilio.twiliochat.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.twilio.twiliochat.ipmessaging.IPMessagingClientManager;

public class TwilioChatApplication extends Application {
  private static TwilioChatApplication instance;
  private IPMessagingClientManager basicClient;

  public static TwilioChatApplication get() {
    return instance;
  }

  public IPMessagingClientManager getIPMessagingClient() {
    return this.basicClient;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    TwilioChatApplication.instance = this;
    basicClient = new IPMessagingClientManager(getApplicationContext());

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    Parse.initialize(this);

    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
