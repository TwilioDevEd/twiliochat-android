package com.twilio.twiliochat.activities;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.twilio.twiliochat.R;

public class LoginActivityTest extends ActivityUnitTestCase<LoginActivity> {
  private LoginActivity loginActivity;
  private Instrumentation instrumentation;

  public LoginActivityTest() {
    super(LoginActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    instrumentation = getInstrumentation();
    Context context = instrumentation.getTargetContext();
    context.setTheme(R.style.AppTheme);
    loginActivity = launchActivity(context.getPackageName(), LoginActivity.class, null);
    setActivity(loginActivity);
    instrumentation.waitForIdleSync();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    loginActivity.finish();
  }

  @MediumTest
  public void testCreateAccountButtonChangesUI() throws Throwable {
    final Button createAccountButton =
        (Button) loginActivity.findViewById(R.id.buttonCreateAccount);
    final Button loginButton = (Button) loginActivity.findViewById(R.id.buttonLogin);
    final LinearLayout emailLayout = (LinearLayout) loginActivity.findViewById(R.id.layoutEmail);
    final LinearLayout fullNameLayout =
        (LinearLayout) loginActivity.findViewById(R.id.layoutFullName);
    assertEquals("Login", loginButton.getText().toString());
    assertEquals(View.GONE, fullNameLayout.getVisibility());
    assertEquals(View.GONE, emailLayout.getVisibility());

    loginActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        createAccountButton.performClick();
      }
    });
    instrumentation.waitForIdleSync();

    assertEquals("Register", loginButton.getText().toString());
    assertEquals(View.VISIBLE, emailLayout.getVisibility());
    assertEquals(View.VISIBLE, fullNameLayout.getVisibility());
  }

  @MediumTest
  public void testLaunchRecoverPasswordIntent() {
    final Button forgotPasswordButton =
        (Button) loginActivity.findViewById(R.id.buttonForgotPassword);

    loginActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        forgotPasswordButton.performClick();
      }
    });
    instrumentation.waitForIdleSync();

    final Intent launchIntent = getStartedActivityIntent();
    //assertNotNull(launchIntent);
  }
}
