package com.twilio.twiliochat.ipmessaging;

public interface LoginListener {
  public void onLoginStarted();

  public void onLoginFinished();

  public void onLoginError(String errorMessage);
}
