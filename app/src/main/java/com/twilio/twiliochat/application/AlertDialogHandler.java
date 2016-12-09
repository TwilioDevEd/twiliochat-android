package com.twilio.twiliochat.application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.twilio.twiliochat.R;
import com.twilio.twiliochat.chat.listeners.InputOnClickListener;

public class AlertDialogHandler {
  public static void displayAlertWithMessage(String message, Context context) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message).setCancelable(false).setPositiveButton("OK", null);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayAlertWithHandler(String message, Context context,
      DialogInterface.OnClickListener handler) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message).setCancelable(false).setPositiveButton("OK", handler);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayCancellableAlertWithHandler(String message, Context context,
      DialogInterface.OnClickListener handler) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message).setPositiveButton("OK", handler).setNegativeButton("Cancel", null);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayInputDialog(String message, Context context,
      final InputOnClickListener handler) {
    LayoutInflater li = LayoutInflater.from(context);
    View promptsView = li.inflate(R.layout.input_dialog_view, null);

    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setView(promptsView);

    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextUserInput);
    final TextView promptMessage = (TextView) promptsView.findViewById(R.id.textViewMessage);
    promptMessage.setText(message);


    builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        handler.onClick(userInput.getText().toString());
      }
    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    });

    AlertDialog alertDialog = builder.create();

    alertDialog.show();
  }
}
