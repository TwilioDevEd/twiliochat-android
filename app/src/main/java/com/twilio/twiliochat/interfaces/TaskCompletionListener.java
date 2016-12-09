package com.twilio.twiliochat.interfaces;

public interface TaskCompletionListener<T, U> {

    void onSuccess(T t);

    void onError(U u);
}
