package com.twilio.twiliochat.interfaces;

import java.util.List;

import com.twilio.chat.Channel;

public interface LoadChannelListener {
  public void onChannelsFinishedLoading(List<Channel> channels);
}
