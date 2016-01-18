package com.twilio.twiliochat.interfaces;

public interface LoginListener {
  public void onLoginFinished();

  public void onLoginError(String errorMessage);
}
