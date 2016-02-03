package com.twilio.twiliochat.ipmessaging;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.interfaces.FetchTokenListener;
import com.twilio.twiliochat.interfaces.LoginListener;
import com.twilio.twiliochat.util.SessionManager;

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
          public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
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
    TokenRequest tokenRequest = new TokenRequest(listener);
    tokenRequest.execute();
  }

  @Override
  public void onChannelAdd(Channel channel) {}

  @Override
  public void onChannelChange(Channel channel) {}

  @Override
  public void onChannelDelete(Channel channel) {}

  @Override
  public void onError(int i, String s) {}

  @Override
  public void onAttributesChange(String s) {}

  @Override
  public void onChannelHistoryLoaded(Channel channel) {}

  @Override
  public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {}

  @Override
  public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {}

  @Override
  public void onError(TwilioAccessManager twilioAccessManager, String s) {}

  private class TokenRequest extends AsyncTask<Void, Void, JSONObject> {
    String username;
    FetchTokenListener handler;

    public TokenRequest(FetchTokenListener handler) {
      this.username = SessionManager.getInstance().getUsername();
      this.handler = handler;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
      String requestUrl = getStringResource(R.string.token_url);
      String response = getHTTPResponse(requestUrl, getTokenRequestParams(username));
      JSONObject jsonResponse = null;

      try {
        jsonResponse = new JSONObject(response);
      } catch (JSONException e) {
        return null;
      }

      return jsonResponse;
    }

    @Override
    protected void onPostExecute(JSONObject jsonResponse) {
      if (jsonResponse == null) {
        handler.fetchTokenFailure(new Exception("unable to fetch token"));
        return;
      }
      String token = null;
      try {
        token = jsonResponse.getString("token");
      } catch (JSONException e) {
        handler.fetchTokenFailure(new Exception("nuable to fetch token"));
        return;
      }

      handler.fetchTokenSuccess(token);
    }

    private String getHTTPResponse(String url, String params) {
      final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

      OkHttpClient client = new OkHttpClient();
      RequestBody body = RequestBody.create(JSONType, params);
      Request request = new Request.Builder().url(url).post(body).build();

      String responseString = null;
      Response response = null;
      try {
        response = client.newCall(request).execute();
        responseString = response.body().string();
      } catch (IOException e) {
        return null;
      }
      return responseString;
    }

    private String getStringResource(int id) {
      Resources resources = TwilioChatApplication.get().getResources();
      return resources.getString(id);
    }

    private String getTokenRequestParams(String username) {
      JSONObject obj = new JSONObject();
      String androidID =
          Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

      try {
        obj.put("username", username);
        obj.put("device", androidID);
      } catch (JSONException e) {
        return null;
      }

      return obj.toString();
    }
  }
}
