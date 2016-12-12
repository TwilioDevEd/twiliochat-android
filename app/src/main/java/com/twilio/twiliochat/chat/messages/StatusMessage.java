package com.twilio.twiliochat.chat.messages;


public class StatusMessage implements ChatMessage {
  private String author = "";

  public StatusMessage() {
  }

  public StatusMessage(String author) {
    this.author = author;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getTimeStamp() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getMessageBody() {
    throw new UnsupportedOperationException();
  }
}
