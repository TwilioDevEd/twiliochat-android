package com.twilio.twiliochat.messaging;

import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.Message;

public class StatusMessage implements Message {
  private String author = "";
  private String timeStamp = "";
  private String messageBody = "";

  public StatusMessage() {}

  public StatusMessage(String author, String timeStamp, String messageBody) {
    this.author = author;
    this.timeStamp = timeStamp;
    this.messageBody = messageBody;
  }

  @Override
  public String getSid() {
    return "no_sid";
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getTimeStamp() {
    return timeStamp;
  }

  @Override
  public String getMessageBody() {
    return messageBody;
  }

  @Override
  public void updateMessageBody(String s, Constants.StatusListener statusListener) {
    messageBody = s;
    statusListener.onSuccess();
  }

  @Override
  public String getChannelSid() {
    return null;
  }

  @Override
  public long getMessageIndex() {
    return 0;
  }
}
