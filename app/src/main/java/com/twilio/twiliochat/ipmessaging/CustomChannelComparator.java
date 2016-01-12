package com.twilio.twiliochat.ipmessaging;

import com.twilio.ipmessaging.Channel;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;

import java.util.Comparator;

public class CustomChannelComparator implements Comparator<Channel> {
  private String defaultChannelName;

  CustomChannelComparator() {
    defaultChannelName = TwilioChatApplication.get().getResources().getString(R.string.default_channel_name);
  }
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
