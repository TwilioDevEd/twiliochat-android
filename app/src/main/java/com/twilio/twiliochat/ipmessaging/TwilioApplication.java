package com.twilio.twiliochat.ipmessaging;

import android.app.Application;

public class TwilioApplication extends Application {

	private static TwilioApplication instance;
	private IPMessagingClient basicClient;

	public static TwilioApplication get() {
		return instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TwilioApplication.instance = this;
		basicClient = new IPMessagingClient(getApplicationContext());
	}
	
	public IPMessagingClient getBasicClient() {
		return this.basicClient;
	}
}
