package com.twilio.twiliochat.chat.channels;

import com.twilio.chat.Channel;

import java.util.List;

public interface LoadChannelListener {
  public void onChannelsFinishedLoading(List<Channel> channels);
}
