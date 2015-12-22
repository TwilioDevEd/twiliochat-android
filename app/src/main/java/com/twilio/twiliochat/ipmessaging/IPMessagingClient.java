package com.twilio.twiliochat.ipmessaging;

import android.content.Context;
import android.os.Handler;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;

public class IPMessagingClient implements IPMessagingClientListener, TwilioAccessManagerListener {
  private String capabilityToken;
  private long nativeClientParam;
  private TwilioIPMessagingClient ipMessagingClient;
  private Channel[] channels;
  private Context context;
  private TwilioAccessManager acessMgr;
  private Handler loginListenerHandler;
  private String urlString;

  public IPMessagingClient(Context context) {
    this.context = context;
  }

  public IPMessagingClient() {
  }

  public String getCapabilityToken() {
    return capabilityToken;
  }

  public void setCapabilityToken(String capabilityToken) {
    this.capabilityToken = capabilityToken;
  }

  public void conectClient() {
    
  }

  @Override
  public void onChannelAdd(Channel channel) {

  }

  @Override
  public void onChannelChange(Channel channel) {

  }

  @Override
  public void onChannelDelete(Channel channel) {

  }

  @Override
  public void onError(int i, String s) {

  }

  @Override
  public void onAttributesChange(String s) {

  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {

  }

  @Override
  public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {

  }

  @Override
  public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {

  }

  @Override
  public void onError(TwilioAccessManager twilioAccessManager, String s) {

  }
}