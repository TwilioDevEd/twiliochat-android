package com.twilio.twiliochat.messaging;

import com.twilio.chat.Message;

import java.util.ArrayList;
import java.util.List;

public interface ChatMessage {

  String getMessageBody();

  String getAuthor();

  String getTimeStamp();
}
