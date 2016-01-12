package com.twilio.twiliochat.ipmessaging;

import com.parse.ParseException;

public interface FetchTokenListener {
  public void fetchTokenSuccess(String token);

  public void fetchTokenFailure(ParseException e);
}
