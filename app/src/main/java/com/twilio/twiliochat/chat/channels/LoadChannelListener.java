package com.twilio.twiliochat.chat.channels;

import java.util.List;

import com.twilio.chat.Channel;

public interface LoadChannelListener {
  public void onChannelsFinishedLoading(List<Channel> channels);
}
