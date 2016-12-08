package com.twilio.twiliochat.messaging;


public class StatusMessage implements ChatMessage {
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
}
