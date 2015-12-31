package com.twilio.twiliochat.ipmessaging;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Channels;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChannelManager implements IPMessagingClientListener {
  private static ChannelManager sharedManager = new ChannelManager();
  public static ChannelManager getInstance() {
    return sharedManager;
  }

  private IPMessagingClientManager client;
  public Channel generalChannel;

  private List<Channel> channels;
  private Channels channelsObject;
  private Channel[] channelArray;
  private IPMessagingClientListener listener;
  private String defaultChannelName;
  private String defaultChannelUniqueName;
  private Handler handler;

  private ChannelManager() {
    this.client = TwilioChatApplication.get().getIPMessagingClient();
    this.listener = this;
    defaultChannelName = getStringResource(R.string.default_channel_name);
    defaultChannelUniqueName = getStringResource(R.string.default_channel_unique_name);
    handler = setupListenerHandler();
  }

  public List<Channel> getChannels() {
    return channels;
  }

  public void populateChannels(final LoadChannelListener listener) {
    if (this.client == null) {
      return;
    }
    handler.post(new Runnable() {
      @Override
      public void run() {
        client.setClientListener(ChannelManager.this);
        channelsObject = client.getIpMessagingClient().getChannels();
        if (channelsObject != null) {
          channelsObject.loadChannelsWithListener(new Constants.StatusListener() {
            @Override
            public void onError() {
              System.out.println("Failed to loadChannelsWithListener");
            }

            @Override
            public void onSuccess() {
              System.out.println("Successfully loadChannelsWithListener.");
              channels = new ArrayList<>();

              channelArray = channelsObject.getChannels();
              if (ChannelManager.this.channels != null && channelArray != null) {
                ChannelManager.this.channels.addAll(Arrays.asList(channelArray));
                Collections.sort(ChannelManager.this.channels, new CustomChannelComparator());
                listener.onChannelsFinishedLoading(ChannelManager.this.channels);
              }
            }
          });
        }
      }
    });
  }

  public void joinGeneralChannelWithCompletion(final Constants.StatusListener listener) {
    if (this.channels == null) {
      listener.onError();
      return;
    }
    this.generalChannel = channelsObject.getChannelByUniqueName(defaultChannelName);
    if (this.generalChannel != null) {
      joinGeneralChannelWithUniqueName(null, listener);
    }
    else {
      createGeneralChannelWithCompletion(new Constants.StatusListener() {
        @Override
        public void onSuccess() {
          joinGeneralChannelWithUniqueName(defaultChannelName, listener);
        }
        @Override
        public void onError() {
          if (listener != null) {
            listener.onError();
          }
        }
      });
    }
  }

  private void joinGeneralChannelWithUniqueName(final String uniqueName, final Constants.StatusListener listener) {
    if (this.generalChannel == null) {
      if (listener != null) {
        listener.onError();
      }
      return;
    }
    this.generalChannel.join(new Constants.StatusListener() {
      @Override
      public void onSuccess() {
        if (uniqueName != null) {
          setGeneralChannelUniqueNameWithCompletion(listener);
          return;
        }
        if (listener != null) {
          listener.onSuccess();
        }
      }
      @Override
      public void onError() {
        listener.onError();
      }
    });
  }

  private void createGeneralChannelWithCompletion(final Constants.StatusListener listener) {
    this.channelsObject
        .createChannel(defaultChannelName, Channel.ChannelType.CHANNEL_TYPE_PUBLIC, new Constants.CreateChannelListener() {
          @Override
          public void onCreated(Channel channel) {
            ChannelManager.this.generalChannel = channel;
            ChannelManager.this.channels.add(channel);
            if (listener != null) {
              listener.onSuccess();
            }
          }
          @Override
          public void onError() {
            listener.onError();
          }
        });
  }

  private void setGeneralChannelUniqueNameWithCompletion(final Constants.StatusListener listener) {
    this.generalChannel.setUniqueName(defaultChannelUniqueName, new Constants.StatusListener() {
      @Override
      public void onSuccess() {
        if (listener != null) {
          listener.onSuccess();
        }
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
  public void onError(int i, String s) {
    if (listener != null) {
      listener.onError(i, s);
    }
  }

  @Override
  public void onAttributesChange(String s) {
    if (listener != null) {
      listener.onAttributesChange(s);
    }
  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {
    if (listener != null) {
      listener.onChannelHistoryLoaded(channel);
    }
  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    }
    else if((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    }
    else {
      handler = null;
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }

  private class CustomChannelComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel lhs, Channel rhs) {
      if (lhs.getFriendlyName().contentEquals(defaultChannelName)) {
        return -100;
      }
      else if (rhs.getFriendlyName().contentEquals(defaultChannelName)) {
        return 100;
      }
      return lhs.getFriendlyName().compareTo(rhs.getFriendlyName());
    }
  }
}
