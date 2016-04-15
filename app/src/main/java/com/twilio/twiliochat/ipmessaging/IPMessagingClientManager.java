package com.twilio.twiliochat.ipmessaging;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.provider.Settings;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.ErrorInfo;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;
import com.twilio.ipmessaging.UserInfo;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.interfaces.FetchTokenListener;
import com.twilio.twiliochat.interfaces.LoginListener;
import com.twilio.twiliochat.util.SessionManager;

public class IPMessagingClientManager implements IPMessagingClientListener {
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

  public void setIpMessagingClient(TwilioIPMessagingClient client) {
    this.ipMessagingClient = client;
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
      public void fetchTokenFailure(Exception e) {
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
          public void onTokenExpired(TwilioAccessManager twilioAccessManager) {
            System.out.println("token expired.");
            fetchAccessToken(new FetchTokenListener() {
              @Override
              public void fetchTokenSuccess(String token) {
                IPMessagingClientManager.this.accessManager.updateToken(token);
              }

              @Override
              public void fetchTokenFailure(Exception e) {
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
    JSONObject obj = new JSONObject(getTokenRequestParams());
    String requestUrl = getStringResource(R.string.token_url);
    JsonObjectRequest jsonObjReq =
        new JsonObjectRequest(Method.POST, requestUrl, obj, new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {
            String token = null;
            try {
              token = response.getString("token");
            } catch (JSONException e) {
              e.printStackTrace();
              listener.fetchTokenFailure(new Exception("Failed to parse token JSON response"));
            }
            listener.fetchTokenSuccess(token);
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {
            listener.fetchTokenFailure(new Exception("Failed to fetch token"));
          }
        });
    jsonObjReq.setShouldCache(false);
    TokenRequest.getInstance().addToRequestQueue(jsonObjReq);
  }

  private String getStringResource(int id) {
    Resources resources = TwilioChatApplication.get().getResources();
    return resources.getString(id);
  }

  private Map<String, String> getTokenRequestParams() {
    String androidId =
        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    Map<String, String> params = new HashMap<>();
    params.put("deviceId", androidId);
    params.put("identity", SessionManager.getInstance().getUsername());
    return params;
  }

  @Override
  public void onChannelAdd(Channel channel) {}

  @Override
  public void onChannelChange(Channel channel) {}

  @Override
  public void onChannelDelete(Channel channel) {}

  @Override
  public void onUserInfoChange(UserInfo userInfo) {}

  @Override
  public void onError(ErrorInfo errorInfo) {}

  @Override
  public void onAttributesChange(String s) {}

  @Override
  public void onChannelHistoryLoaded(Channel channel) {}
}
