package com.twilio.twiliochat.ipmessaging;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

public class IPMessagingClientManager
    implements IPMessagingClientListener, TwilioAccessManagerListener {
  private final String TOKEN_KEY = "token";
  private final Handler handler = new Handler();
  private String capabilityToken;
  private TwilioIPMessagingClient ipMessagingClient;
  private Context context;
  private TwilioAccessManager accessManager;

  public IPMessagingClientManager(Context context) {
    this.context = context;
  }

  public IPMessagingClientManager() {}

  public String getCapabilityToken() {
    return capabilityToken;
  }

  public void setCapabilityToken(String capabilityToken) {
    this.capabilityToken = capabilityToken;
    if (this.accessManager != null) {
      this.accessManager.updateToken(capabilityToken);
    }
  }

  public void setClientListener(IPMessagingClientListener listener) {
    if (this.ipMessagingClient != null) {
      this.ipMessagingClient.setListener(listener);
    }
  }

  public TwilioIPMessagingClient getIpMessagingClient() {
    return this.ipMessagingClient;
  }

  public void connectClient(final LoginListener listener) {
    TwilioIPMessagingSDK.setLogLevel(android.util.Log.DEBUG);
    if (!TwilioIPMessagingSDK.isInitialized()) {
      TwilioIPMessagingSDK.initializeSDK(context, new Constants.InitListener() {
        @Override
        public void onInitialized() {
          createClientWithAccessManager(listener);
        }

        @Override
        public void onError(Exception error) {
          System.out.println("Error initializing the SDK :" + error.getMessage());
        }
      });
    } else {
      createClientWithAccessManager(listener);
    }
  }

  private void createClientWithAccessManager(final LoginListener listener) {
    fetchAccessToken(new FetchTokenListener() {
      @Override
      public void fetchTokenSuccess(String token) {
        initializeClientWithToken(token, listener);
      }

      @Override
      public void fetchTokenFailure(ParseException e) {
        if (listener != null) {
          listener.onLoginError(e.getLocalizedMessage());
        }
      }
    });
  }

  private void initializeClientWithToken(String token, final LoginListener listener) {
    this.accessManager =
        TwilioAccessManagerFactory.createAccessManager(token, new TwilioAccessManagerListener() {
          @Override
          public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
            System.out.println("token expired.");
            fetchAccessToken(new FetchTokenListener() {
              @Override
              public void fetchTokenSuccess(String token) {
                IPMessagingClientManager.this.accessManager.updateToken(token);
              }

              @Override
              public void fetchTokenFailure(ParseException e) {
                System.out.println("Error trying to fetch token: " + e.getLocalizedMessage());
              }
            });
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

    ipMessagingClient =
        TwilioIPMessagingSDK.createIPMessagingClientWithAccessManager(this.accessManager, this);
    if (listener != null) {
      listener.onLoginFinished();
    }
  }

  private void fetchAccessToken(final FetchTokenListener listener) {
    ParseCloud.callFunctionInBackground(TOKEN_KEY, getTokenRequestParams(),
        new FunctionCallback<Object>() {
          @Override
          public void done(Object object, ParseException e) {
            if (e != null) {
              listener.fetchTokenFailure(e);
              return;
            }
            Map<String, String> result = (HashMap<String, String>) object;
            String token = result.get(TOKEN_KEY);
            IPMessagingClientManager.this.capabilityToken = capabilityToken;
            listener.fetchTokenSuccess(token);
          }
        });
  }

  private Map<String, String> getTokenRequestParams() {
    String android_id =
        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    Map<String, String> params = new HashMap<>();
    params.put("device", android_id);

    return params;
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
