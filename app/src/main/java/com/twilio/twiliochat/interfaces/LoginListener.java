package com.twilio.twiliochat.interfaces;

public interface LoginListener {
  public void onLoginStarted();

  public void onLoginFinished();

  public void onLoginError(String errorMessage);
}
