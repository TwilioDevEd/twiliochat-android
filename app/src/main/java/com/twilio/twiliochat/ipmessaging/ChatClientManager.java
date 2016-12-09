package com.twilio.twiliochat.ipmessaging;

import android.content.Context;
import android.os.Handler;

import com.twilio.accessmanager.AccessManager;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.UserInfo;
import com.twilio.twiliochat.interfaces.FetchTokenListener;

public class ChatClientManager extends CallbackListener<ChatClient> implements ChatClientListener, AccessManager.Listener, AccessManager.TokenUpdateListener {
  private final String TOKEN_KEY = "token";
  private final Handler handler = new Handler();
  private String capabilityToken;
  private ChatClient chatClient;
  private Context context;
  private AccessManager accessManager;
  private AccessTokenFetcher accessTokenFetcher;

  private TaskCompletionListener<Void, String> loginListener;

  public ChatClientManager(Context context) {
    this.context = context;
    this.accessTokenFetcher = new AccessTokenFetcher(this.context);
  }

  public ChatClientManager() {}

  @Override
  public void onSuccess(ChatClient chatClient) {
    this.chatClient = chatClient;
    this.loginListener.onSuccess(null);
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    this.loginListener.onError(errorInfo.getErrorText());
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

  public void connectClient(final TaskCompletionListener listener) {
    ChatClient.setLogLevel(android.util.Log.DEBUG);
    createClient(listener);
  }

  private void createClient(final TaskCompletionListener listener) {
    accessTokenFetcher.fetch(new FetchTokenListener() {
      @Override
      public void fetchTokenSuccess(String token) {
        initializeClientWithToken(token, listener);
      }

      @Override
      public void fetchTokenFailure(Exception e) {
        if (listener != null) {
          listener.onError(e.getLocalizedMessage());
        }
      }
    });
  }

  private void createAccessManager(String token) {
    this.accessManager = new AccessManager(token, this);
    accessManager.addTokenUpdateListener(this);
  }

  private void initializeClientWithToken(String token, final TaskCompletionListener listener) {
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
    accessTokenFetcher.fetch(new FetchTokenListener() {
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
