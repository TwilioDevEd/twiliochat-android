package com.twilio.twiliochat.chat.listeners;

public interface TaskCompletionListener<T, U> {

  void onSuccess(T t);

  void onError(U u);
}
