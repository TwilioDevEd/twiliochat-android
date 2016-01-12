package com.twilio.twiliochat.ipmessaging;

import java.util.List;

import com.twilio.ipmessaging.Channel;

public interface LoadChannelListener {
  public void onChannelsFinishedLoading(List<Channel> channels);
}
