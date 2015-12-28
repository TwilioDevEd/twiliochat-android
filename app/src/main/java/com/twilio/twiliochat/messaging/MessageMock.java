package com.twilio.twiliochat.messaging;

public class MessageMock {
  private String id;
  private String textBody;
  private String sender;

  public MessageMock() {

  }

  public MessageMock(String textBody) {
    this.textBody = textBody;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTextBody() {
    return textBody;
  }

  public void setTextBody(String textBody) {
    this.textBody = textBody;
  }
}
