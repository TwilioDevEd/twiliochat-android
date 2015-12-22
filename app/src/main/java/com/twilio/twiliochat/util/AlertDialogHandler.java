package com.twilio.twiliochat.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;

import com.twilio.twiliochat.R;

public class AlertDialogHandler {
  public static void displayAlertWithMessage(String message, Context context) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message)
        .setCancelable(false)
        .setPositiveButton("OK", null);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayAlertWithHandler(String message, Context context, DialogInterface.OnClickListener handler) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message)
        .setCancelable(false)
        .setPositiveButton("OK", handler);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayCancellableAlertWithHandler(String message, Context context, DialogInterface.OnClickListener handler) {
    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message)
        .setPositiveButton("OK", handler)
        .setNegativeButton("Cancel", null);

    AlertDialog alert = builder.create();
    alert.show();
  }
}
