package com.twilio.twiliochat.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.twilio.twiliochat.ipmessaging.IPMessagingClient;

public class TwilioChatApplication extends Application {
  private static TwilioChatApplication instance;
  private IPMessagingClient basicClient;

  public static TwilioChatApplication get() {
    return instance;
  }

  public IPMessagingClient getIPMessagingClient() {
    return this.basicClient;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    TwilioChatApplication.instance = this;
    basicClient = new IPMessagingClient(getApplicationContext());

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this);

    ParseACL defaultACL = new ParseACL();
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
