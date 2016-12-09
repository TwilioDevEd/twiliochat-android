package com.twilio.twiliochat.ipmessaging;

import android.content.Context;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ErrorInfo;
import com.twilio.twiliochat.interfaces.TaskCompletionListener;

public class ChatClientBuilder extends CallbackListener<ChatClient>{

    private AccessTokenFetcher accessTokenFetcher;
    private Context context;
    private TaskCompletionListener<ChatClient, String> buildListener;

    public ChatClientBuilder(Context context) {
        this.accessTokenFetcher = new AccessTokenFetcher(context);
        this.context = context;
    }

    public void build(String token, final TaskCompletionListener<ChatClient, String> listener) {
        ChatClient.Properties props =
                new ChatClient.Properties.Builder()
                        .setSynchronizationStrategy(ChatClient.SynchronizationStrategy.CHANNELS_LIST)
                        .setRegion("us1")
                        .createProperties();

        this.buildListener = listener;
        ChatClient.create(context.getApplicationContext(),
                token,
                props,
                this);
    }


    @Override
    public void onSuccess(ChatClient chatClient) {
        this.buildListener.onSuccess(chatClient);
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        this.buildListener.onError(errorInfo.getErrorText());
    }
}
