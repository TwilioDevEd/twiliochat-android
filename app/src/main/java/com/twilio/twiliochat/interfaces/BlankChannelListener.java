package com.twilio.twiliochat.interfaces;

import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.ChannelListener;
import com.twilio.ipmessaging.Member;
import com.twilio.ipmessaging.Message;

import java.util.Map;

public class BlankChannelListener implements ChannelListener {
  @Override
  public void onMessageAdd(Message message) {
    // Do nothing intentionally
  }

  @Override
  public void onMessageChange(Message message) {
    // Do nothing intentionally
  }

  @Override
  public void onMessageDelete(Message message) {
    // Do nothing intentionally
  }

  @Override
  public void onMemberJoin(Member member) {
    // Do nothing intentionally
  }

  @Override
  public void onMemberChange(Member member) {
    // Do nothing intentionally
  }

  @Override
  public void onMemberDelete(Member member) {
    // Do nothing intentionally
  }

  @Override
  public void onAttributesChange(Map<String, String> map) {
    // Do nothing intentionally
  }

  @Override
  public void onTypingStarted(Member member) {
    // Do nothing intentionally
  }

  @Override
  public void onTypingEnded(Member member) {
    // Do nothing intentionally
  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {
    // Do nothing intentionally
  }
}
