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
import com.twilio.accessmanager.AccessManager;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.UserInfo;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.interfaces.FetchTokenListener;
import com.twilio.twiliochat.interfaces.LoginListener;
import com.twilio.twiliochat.util.SessionManager;

public class ChatClientManager extends CallbackListener<ChatClient> implements ChatClientListener, AccessManager.Listener, AccessManager.TokenUpdateListener {
  private final String TOKEN_KEY = "token";
  private final Handler handler = new Handler();
  private String capabilityToken;
  private ChatClient chatClient;
  private Context context;
  private AccessManager accessManager;

  private LoginListener loginListener;

  public ChatClientManager(Context context) {
    this.context = context;
  }

  public ChatClientManager() {}

  @Override
  public void onSuccess(ChatClient chatClient) {
    this.chatClient = chatClient;
    this.loginListener.onLoginFinished();
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    this.loginListener.onLoginError(errorInfo.getErrorText());
  }

  public String getCapabilityToken() {
    return capabilityToken;
  }

  public void setCapabilityToken(String capabilityToken) {
    this.capabilityToken = capabilityToken;
    if (this.accessManager != null) {
      this.accessManager.updateToken(capabilityToken);
    }
  }

  public void setClientListener(ChatClientListener listener) {
    if (this.chatClient != null) {
      this.chatClient.setListener(listener);
    }
  }

  public ChatClient getChatClient() {
    return this.chatClient;
  }

  public void setChatClient(ChatClient client) {
    this.chatClient = client;
  }

  public void connectClient(final LoginListener listener) {
    ChatClient.setLogLevel(android.util.Log.DEBUG);
    createClientWithAccessManager(listener);
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

  private void createAccessManager(String token) {
    this.accessManager = new AccessManager(token, this);
    accessManager.addTokenUpdateListener(this);
  }

  private void initializeClientWithToken(String token, final LoginListener listener) {
    createAccessManager(token);

    ChatClient.Properties props =
            new ChatClient.Properties.Builder()
                    .setSynchronizationStrategy(ChatClient.SynchronizationStrategy.CHANNELS_LIST)
                    .setRegion("us1")
                    .createProperties();

    this.loginListener = listener;
    ChatClient.create(context.getApplicationContext(),
            token,
            props,
            this);
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
  public void onChannelAdd(Channel channel) {
    
  }

  @Override
  public void onChannelInvite(Channel channel) {

  }

  @Override
  public void onChannelChange(Channel channel) {

  }

  @Override
  public void onChannelDelete(Channel channel) {

  }

  @Override
  public void onChannelSynchronizationChange(Channel channel) {

  }

  @Override
  public void onUserInfoChange(UserInfo userInfo, UserInfo.UpdateReason updateReason) {

  }

  @Override
  public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

  }

  @Override
  public void onToastNotification(String s, String s1) {

  }

  @Override
  public void onToastSubscribed() {

  }

  @Override
  public void onToastFailed(ErrorInfo errorInfo) {

  }

  @Override
  public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

  }

  /** AccessManager.Listener methods **/
  @Override
  public void onTokenWillExpire(AccessManager accessManager) {

  }

  @Override
  public void onTokenExpired(AccessManager accessManager) {
    System.out.println("token expired.");
    fetchAccessToken(new FetchTokenListener() {
      @Override
      public void fetchTokenSuccess(String token) {
        ChatClientManager.this.accessManager.updateToken(token);
      }

      @Override
      public void fetchTokenFailure(Exception e) {
        System.out.println("Error trying to fetch token: " + e.getLocalizedMessage());
      }
    });
  }

  @Override
  public void onError(AccessManager accessManager, String s) {
    System.out.println("token error: " + s);
  }

  @Override
  public void onTokenUpdated(String s) {
    System.out.println("token updated.");
  }

  /** **/
  public void shutdown() {
    chatClient.shutdown();
  }
}
