package com.twilio.twiliochat.interfaces;

public interface FetchTokenListener {
  public void fetchTokenSuccess(String token);

  public void fetchTokenFailure(Exception e);
}
