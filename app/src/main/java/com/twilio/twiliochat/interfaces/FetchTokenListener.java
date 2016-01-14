package com.twilio.twiliochat.interfaces;

import com.parse.ParseException;

public interface FetchTokenListener {
  public void fetchTokenSuccess(String token);

  public void fetchTokenFailure(ParseException e);
}
