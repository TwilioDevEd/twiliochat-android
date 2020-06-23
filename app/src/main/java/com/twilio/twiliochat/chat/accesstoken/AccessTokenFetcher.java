package com.twilio.twiliochat.chat.accesstoken;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.SessionManager;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenFetcher {

  private Context context;

  public AccessTokenFetcher(Context context) {
    this.context = context;
  }

  public void fetch(final TaskCompletionListener<String, String> listener) {
    JSONObject obj = new JSONObject(getTokenRequestParams(context));
    String identity = SessionManager.getInstance().getUsername();
    String requestUrl = getStringResource(R.string.token_url) + "?identity=" + identity;
    Log.d(TwilioChatApplication.TAG, "Requesting access token from: " + requestUrl);

    JsonObjectRequest jsonObjReq =
        new JsonObjectRequest(Request.Method.GET, requestUrl, obj, new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {
            String token = null;
            try {
              token = response.getString("token");
            } catch (JSONException e) {
              Log.e(TwilioChatApplication.TAG, e.getLocalizedMessage(), e);
              listener.onError("Failed to parse token JSON response");
            }
            listener.onSuccess(token);
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e(TwilioChatApplication.TAG, error.getLocalizedMessage(), error);
            listener.onError("Failed to fetch token");
          }
        });
    jsonObjReq.setShouldCache(false);
    TokenRequest.getInstance().addToRequestQueue(jsonObjReq);
  }

  private Map<String, String> getTokenRequestParams(Context context) {
    Map<String, String> params = new HashMap<>();
    params.put("identity", SessionManager.getInstance().getUsername());
    return params;
  }

  private String getStringResource(int id) {
    Resources resources = TwilioChatApplication.get().getResources();
    return resources.getString(id);
  }

}
