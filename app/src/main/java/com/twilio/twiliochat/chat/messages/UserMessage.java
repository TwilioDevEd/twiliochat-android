package com.twilio.twiliochat.chat.messages;

import com.twilio.chat.Message;

public class UserMessage implements ChatMessage {

  private String author = "";
  private String dateCreated = "";
  private String messageBody = "";

  public UserMessage(Message message) {
    this.author = message.getAuthor();
    this.dateCreated = message.getDateCreated();
    this.messageBody = message.getMessageBody();
  }

  @Override
  public String getMessageBody() {
    return messageBody;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getDateCreated() {
    return dateCreated;
  }
}
