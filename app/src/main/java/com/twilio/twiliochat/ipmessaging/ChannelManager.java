package com.twilio.twiliochat.ipmessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Channel.ChannelType;
import com.twilio.ipmessaging.Channels;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.ErrorInfo;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.UserInfo;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.interfaces.LoadChannelListener;

public class ChannelManager implements IPMessagingClientListener {
  private static ChannelManager sharedManager = new ChannelManager();
  public Channel generalChannel;
  private IPMessagingClientManager client;
  private List<Channel> channels;
  private Channels channelsObject;
  private Channel[] channelArray;
  private IPMessagingClientListener listener;
  private String defaultChannelName;
  private String defaultChannelUniqueName;
  private Handler handler;
  private Boolean isRefreshingChannels = false;

  private ChannelManager() {
    this.client = TwilioChatApplication.get().getIPMessagingClient();
    this.listener = this;
    defaultChannelName = getStringResource(R.string.default_channel_name);
    defaultChannelUniqueName = getStringResource(R.string.default_channel_unique_name);
    handler = setupListenerHandler();
  }

  public static ChannelManager getInstance() {
    return sharedManager;
  }

  public List<Channel> getChannels() {
    return channels;
  }

  public String getDefaultChannelName() {
    return this.defaultChannelName;
  }

  public void leaveChannelWithHandler(Channel channel, Constants.StatusListener handler) {
    channel.leave(handler);
  }

  public void deleteChannelWithHandler(Channel channel, Constants.StatusListener handler) {
    channel.destroy(handler);
  }

  public void populateChannels(final LoadChannelListener listener) {
    if (this.client == null || this.isRefreshingChannels) {
      return;
    }
    this.isRefreshingChannels = true;
    handler.post(new Runnable() {
      @Override
      public void run() {
        channelsObject = client.getIpMessagingClient().getChannels();
        if (channelsObject != null) {
          channelsObject.loadChannelsWithListener(new Constants.StatusListener() {
            @Override
            public void onError() {
              ChannelManager.this.isRefreshingChannels = false;
              System.out.println("Failed to loadChannelsWithListener");
            }

            @Override
            public void onSuccess() {
              channels = new ArrayList<>();

              channelArray = channelsObject.getChannels();
              if (ChannelManager.this.channels != null && channelArray != null) {
                ChannelManager.this.channels.addAll(Arrays.asList(channelArray));
                Collections.sort(ChannelManager.this.channels, new CustomChannelComparator());
                ChannelManager.this.isRefreshingChannels = false;
                client.setClientListener(ChannelManager.this);
                listener.onChannelsFinishedLoading(ChannelManager.this.channels);
              }
            }
          });
        }
      }
    });
  }

  public void createChannelWithName(String name, final Constants.StatusListener handler) {
    Map<String, Object> options = new HashMap<>();
    options.put(Constants.CHANNEL_FRIENDLY_NAME, name);
    options.put(Constants.CHANNEL_TYPE, ChannelType.CHANNEL_TYPE_PUBLIC);
    this.channelsObject.createChannel(options, new Constants.CreateChannelListener() {
      @Override
      public void onCreated(Channel channel) {
        handler.onSuccess();
      }

      @Override
      public void onError() {
        handler.onError();
      }
    });
  }

  public void joinOrCreateGeneralChannelWithCompletion(final Constants.StatusListener listener) {
    if (this.channels == null) {
      listener.onError();
      return;
    }
    this.generalChannel = channelsObject.getChannelByUniqueName(defaultChannelUniqueName);
    if (this.generalChannel != null) {
      joinGeneralChannelWithCompletion(listener);
    } else {
      createGeneralChannelWithCompletion(listener);
    }
  }

  private void joinGeneralChannelWithCompletion(final Constants.StatusListener listener) {
    if (this.generalChannel == null) {
      listener.onError();
      return;
    }
    this.generalChannel.join(new Constants.StatusListener() {
      @Override
      public void onSuccess() {
        listener.onSuccess();
      }

      @Override
      public void onError() {
        listener.onError();
      }
    });
  }

  private void createGeneralChannelWithCompletion(final Constants.StatusListener listener) {
    Map<String, Object> options = new HashMap<>();
    options.put(Constants.CHANNEL_FRIENDLY_NAME, defaultChannelName);
    options.put(Constants.CHANNEL_UNIQUE_NAME, defaultChannelUniqueName);
    options.put(Constants.CHANNEL_TYPE, ChannelType.CHANNEL_TYPE_PUBLIC);
    this.channelsObject.createChannel(options, new Constants.CreateChannelListener() {
      @Override
      public void onCreated(Channel channel) {
        ChannelManager.this.generalChannel = channel;
        ChannelManager.this.channels.add(channel);
        joinGeneralChannelWithCompletion(listener);
      }

      @Override
      public void onError() {
        listener.onError();
      }
    });
  }

  public void setChannelListener(IPMessagingClientListener listener) {
    this.listener = listener;
  }

  private String getStringResource(int id) {
    Resources resources = TwilioChatApplication.get().getResources();
    return resources.getString(id);
  }

  @Override
  public void onChannelAdd(Channel channel) {
    if (listener != null) {
      listener.onChannelAdd(channel);
    }
  }

  @Override
  public void onChannelChange(Channel channel) {
    if (listener != null) {
      listener.onChannelChange(channel);
    }
  }

  @Override
  public void onChannelDelete(Channel channel) {
    if (listener != null) {
      listener.onChannelDelete(channel);
    }
  }

  @Override
  public void onChannelSynchronizationChange(Channel channel) {
    if (listener != null) {
      listener.onChannelSynchronizationChange(channel);
    }
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    if (listener != null) {
      listener.onError(errorInfo);
    }
  }

  @Override
  public void onUserInfoChange(UserInfo userInfo) {
    if (listener != null) {
      listener.onUserInfoChange(userInfo);
    }
  }

  @Override
  public void onClientSynchronization(
      TwilioIPMessagingClient.SynchronizationStatus synchronizationStatus) {

  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if ((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    } else if ((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    } else {
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }
}
