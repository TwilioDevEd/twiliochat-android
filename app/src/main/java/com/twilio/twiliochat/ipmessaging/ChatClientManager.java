package com.twilio.twiliochat.ipmessaging;

import android.content.Context;

import com.twilio.accessmanager.AccessManager;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.twiliochat.interfaces.TaskCompletionListener;

public class ChatClientManager implements AccessManager.Listener, AccessManager.TokenUpdateListener {
  private ChatClient chatClient;
  private Context context;
  private AccessManager accessManager;
  private AccessTokenFetcher accessTokenFetcher;
  private ChatClientBuilder chatClientBuilder;


  public ChatClientManager(Context context) {
    this.context = context;
    this.accessTokenFetcher = new AccessTokenFetcher(this.context);
    this.chatClientBuilder = new ChatClientBuilder(this.context);
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

  public void connectClient(final TaskCompletionListener<Void, String> listener) {
    ChatClient.setLogLevel(android.util.Log.DEBUG);

    accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
      @Override
      public void onSuccess(String token) {
        createAccessManager(token);
        buildClient(token, listener);
      }

      @Override
      public void onError(String message) {
        if (listener != null) {
          listener.onError(message);
        }
      }
    });
  }

  private void buildClient(String token, final TaskCompletionListener<Void, String> listener) {
    chatClientBuilder.build(token, new TaskCompletionListener<ChatClient, String>() {
      @Override
      public void onSuccess(ChatClient chatClient) {
        ChatClientManager.this.chatClient = chatClient;
        listener.onSuccess(null);
      }

      @Override
      public void onError(String message) {
        listener.onError(message);
      }
    });
  }

  private void createAccessManager(String token) {
    this.accessManager = new AccessManager(token, this);
    accessManager.addTokenUpdateListener(this);
  }

  /** AccessManager.Listener methods **/
  @Override
  public void onTokenWillExpire(AccessManager accessManager) {

  }

  @Override
  public void onTokenExpired(AccessManager accessManager) {
    System.out.println("token expired.");
    accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
      @Override
      public void onSuccess(String token) {
        ChatClientManager.this.accessManager.updateToken(token);
      }

      @Override
      public void onError(String message) {
        System.out.println("Error trying to fetch token: " + message);
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
