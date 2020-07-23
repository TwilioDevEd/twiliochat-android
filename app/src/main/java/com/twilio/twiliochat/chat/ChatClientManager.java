package com.twilio.twiliochat.chat;

import android.content.Context;

import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.twiliochat.chat.accesstoken.AccessTokenFetcher;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

public class ChatClientManager {
  private ChatClient chatClient;
  private Context context;
  private AccessTokenFetcher accessTokenFetcher;
  private ChatClientBuilder chatClientBuilder;


  public ChatClientManager(Context context) {
    this.context = context;
    this.accessTokenFetcher = new AccessTokenFetcher(this.context);
    this.chatClientBuilder = new ChatClientBuilder(this.context);
  }

  public void addClientListener(ChatClientListener listener) {
    if (this.chatClient != null) {
      this.chatClient.addListener(listener);
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

  public void shutdown() {
    if(chatClient != null) {
      chatClient.shutdown();
    }
  }

  public AccessTokenFetcher getAccessTokenFetcher() {
    return accessTokenFetcher;
  }
}
