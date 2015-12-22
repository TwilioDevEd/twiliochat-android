package com.twilio.twiliochat.ipmessaging;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants.InitListener;
import com.twilio.ipmessaging.Constants.StatusListener;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

import java.util.Arrays;
import java.util.List;

public class IPMessagingClient implements IPMessagingClientListener, TwilioAccessManagerListener {

  private static final String TAG = "IPMessagingClient";
  private String capabilityToken;
  private String gcmToken;
  private long nativeClientParam;
  private TwilioIPMessagingClient ipMessagingClient;
  private Channel[] channels;
  private Context context;
  private TwilioAccessManager acessMgr;
  private Handler loginListenerHandler;
  private String urlString;

  public IPMessagingClient(Context context) {
    super();
    this.context = context;
  }

  public IPMessagingClient() {
    super();
  }

  public String getCapabilityToken() {
    return capabilityToken;
  }

  public void setCapabilityToken(String capabilityToken) {
    this.capabilityToken = capabilityToken;
  }

  public String getGCMToken() {
    return gcmToken;
  }

  public void setGCMToken(String gcmToken) {
    this.gcmToken = gcmToken;
  }

  public void doLogin(final String capabilityToken, final LoginListener listener, String url) {
    this.urlString = url;
    this.loginListenerHandler = setupListenerHandler();
    TwilioIPMessagingSDK.setLogLevel(Log.DEBUG);
    if (!TwilioIPMessagingSDK.isInitialized()) {
      TwilioIPMessagingSDK.initializeSDK(context, new InitListener() {
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
      this.createClientWithAccessManager(listener);
    }

  }

  public List<Channel> getChannelList() {
    List<Channel> list = Arrays.asList(this.channels);
    return list;
  }

  public long getNativeClientParam() {
    return nativeClientParam;
  }

  public void setNativeClientParam(long nativeClientParam) {
    this.nativeClientParam = nativeClientParam;
  }

  @Override
  public void onChannelAdd(Channel channel) {
    if (channel != null) {
      System.out.println("A Channel :" + channel.getFriendlyName() + " got added");
    } else {
      System.out.println("Received onChannelAdd event.");
    }
  }

  @Override
  public void onChannelChange(Channel channel) {
    if (channel != null) {
      System.out.println("Channel Name : " + channel.getFriendlyName() + " got Changed");
    } else {
      System.out.println("received onChannelChange event.");
    }
  }

  @Override
  public void onChannelDelete(Channel channel) {
    if (channel != null) {
      System.out.println("A Channel :" + channel.getFriendlyName() + " got deleted");
    } else {
      System.out.println("received onChannelDelete event.");
    }
  }

  @Override
  public void onError(int errorCode, String errorText) {
    System.out.println("Received onError event.");
  }

  @Override
  public void onAttributesChange(String attributes) {
    System.out.println("Received onAttributesChange event.");
  }

  public TwilioIPMessagingClient getIpMessagingClient() {
    return ipMessagingClient;
  }

  private void createClientWithAccessManager(final LoginListener listener) {
    this.acessMgr = TwilioAccessManagerFactory.createAccessManager(this.capabilityToken, new TwilioAccessManagerListener() {
      @Override
      public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
        Log.d(TAG, "token expired.");
        new GetCapabilityTokenAsyncTask().execute(IPMessagingClient.this.urlString);
      }

      @Override
      public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {
        Log.d(TAG, "token updated.");
      }

      @Override
      public void onError(TwilioAccessManager twilioAccessManager, String s) {
        Log.d(TAG, "token error: " + s);
      }
    });

    ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithAccessManager(IPMessagingClient.this.acessMgr, IPMessagingClient.this);
         /*if(ipMessagingClient != null) {
             ipMessagingClient.setListener(IPMessagingClient.this);
         	Intent intent = new Intent(context,ChannelActivity.class);
         	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
         	ipMessagingClient.setIncomingIntent(pendingIntent);
         	IPMessagingClient.this.loginListenerHandler.post(new Runnable() {
					@Override
					public void run() {
						if(listener != null) {
							listener.onLoginFinished();
			        	}
					}
				});
     	} else {
     		listener.onLoginError("ipMessagingClientWithAccessManager is null");
     	}
     	*/
  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {
    System.out.println("Received onChannelHistoryLoaded callback " + channel.getFriendlyName());
  }

  @Override
  public void onAccessManagerTokenExpire(TwilioAccessManager arg0) {
    System.out.println("Received AccessManager:onAccessManagerTokenExpire.");
  }

  @Override
  public void onError(TwilioAccessManager arg0, String arg1) {
    System.out.println("Received AccessManager:onError.");
  }

  @Override
  public void onTokenUpdated(TwilioAccessManager arg0) {
    System.out.println("Received AccessManager:onTokenUpdated.");
  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if ((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    } else if ((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    } else {
      handler = null;
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }

  public interface LoginListener {
    public void onLoginStarted();

    public void onLoginFinished();

    public void onLoginError(String errorMessage);

    public void onLogoutFinished();
  }

  private class GetCapabilityTokenAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      ipMessagingClient.updateToken(null, new StatusListener() {

        @Override
        public void onSuccess() {
          System.out.println("Updated Token was successfull");
        }

        @Override
        public void onError() {
          System.out.println("Updated Token failed");
        }
      });
      acessMgr.updateToken(null);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
      return "capabilityToken";
    }
  }
}