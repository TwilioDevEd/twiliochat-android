package com.twilio.twiliochat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // launch a different activity
        Intent launchIntent = new Intent();
        Class<?> launchActivity;

        launchActivity = getLaunchClass();
        launchIntent.setClass(getApplicationContext(), launchActivity);
        startActivity(launchIntent);

        finish();
    }

    /** return Class name of Activity to show **/
    private Class<?> getLaunchClass()
    {
        return LoginActivity.class;
    }
}
