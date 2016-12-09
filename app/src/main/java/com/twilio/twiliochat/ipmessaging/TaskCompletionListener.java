package com.twilio.twiliochat.ipmessaging;

public interface TaskCompletionListener<T, U> {

    void onSuccess(T t);

    void onError(U u);
}
