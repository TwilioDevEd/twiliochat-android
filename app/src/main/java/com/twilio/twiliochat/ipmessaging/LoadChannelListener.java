package com.twilio.twiliochat.ipmessaging;

import com.twilio.ipmessaging.Channel;

import java.util.List;

public interface LoadChannelListener {
  public void onChannelsFinishedLoading(List<Channel> channels);
}
