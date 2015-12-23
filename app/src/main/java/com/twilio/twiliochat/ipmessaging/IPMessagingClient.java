package com.twilio.twiliochat.ipmessaging;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

public class IPMessagingClient implements IPMessagingClientListener, TwilioAccessManagerListener {
  private String capabilityToken;
  private long nativeClientParam;
  private TwilioIPMessagingClient ipMessagingClient;
  private Channel[] channels;
  private Context context;
  private TwilioAccessManager accessManager;
  private Handler loginListenerHandler;
  private String urlString;

  public IPMessagingClient(Context context) {
    this.context = context;
  }

  public IPMessagingClient() {
  }

  public String getCapabilityToken() {
    return capabilityToken;
  }

  public void setCapabilityToken(String capabilityToken) {
    this.capabilityToken = capabilityToken;
  }

  public void connectClient(final String capabilityToken, final LoginListener listener) {
    this.capabilityToken = capabilityToken;
    this.loginListenerHandler = setupListenerHandler();
    TwilioIPMessagingSDK.setLogLevel(android.util.Log.DEBUG);
    if(!TwilioIPMessagingSDK.isInitialized()) {
      TwilioIPMessagingSDK.initializeSDK(context, new Constants.InitListener()
      {
        @Override
        public void onInitialized()
        {
          createClientWithAccessManager(capabilityToken, listener);
        }

        @Override
        public void onError(Exception error)
        {
          System.out.println("Error initializing the SDK :" + error.getMessage());
        }
      });
    }
    else {
      createClientWithAccessManager(capabilityToken, listener);
    }
  }

  private void createClientWithAccessManager(final String capabilityToken, final LoginListener listener) {
    this.accessManager = TwilioAccessManagerFactory.createAccessManager(capabilityToken, new TwilioAccessManagerListener() {
      @Override
      public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
        System.out.println("token expired.");
        //new GetCapabilityTokenAsyncTask().execute(BasicIPMessagingClient.this.urlString);
      }

      @Override
      public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {
        System.out.println("token updated.");
      }

      @Override
      public void onError(TwilioAccessManager twilioAccessManager, String s) {
        System.out.println("token error: " + s);
      }
    });

    ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithAccessManager(accessManager, this);
    if(ipMessagingClient != null) {
      ipMessagingClient.setListener(this);
      //Intent intent = new Intent(context,ChannelActivity.class);
      //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      //ipMessagingClient.setIncomingIntent(pendingIntent);
      this.loginListenerHandler.post(new Runnable() {
        @Override
        public void run() {
          if (listener != null) {
            listener.onLoginFinished();
          }
        }
      });
    } else {
      listener.onLoginError("ipMessagingClientWithAccessManager is null");
    }
  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    } else if((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    } else {
      handler = null;
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }

  @Override
  public void onChannelAdd(Channel channel) {

  }

  @Override
  public void onChannelChange(Channel channel) {

  }

  @Override
  public void onChannelDelete(Channel channel) {

  }

  @Override
  public void onError(int i, String s) {

  }

  @Override
  public void onAttributesChange(String s) {

  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {

  }

  @Override
  public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {

  }

  @Override
  public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {

  }

  @Override
  public void onError(TwilioAccessManager twilioAccessManager, String s) {

  }
}