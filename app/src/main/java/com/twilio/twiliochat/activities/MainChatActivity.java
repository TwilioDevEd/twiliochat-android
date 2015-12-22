package com.twilio.twiliochat.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.messaging.Message;
import com.twilio.twiliochat.messaging.MessageAdapter;
import com.twilio.twiliochat.util.AlertDialogHandler;

public class MainChatActivity extends AppCompatActivity {
  Context context;
  Button logoutButton;
  Button sendButton;
  TextView usernameTextView;
  ListView messagesListView;
  EditText messageTextEdit;

  MessageAdapter messageAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_chat);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

    context = this;
    logoutButton = (Button) findViewById(R.id.buttonLogout);
    sendButton = (Button) findViewById(R.id.buttonSend);
    usernameTextView = (TextView) findViewById(R.id.textViewUsername);
    messagesListView = (ListView) findViewById(R.id.listViewMessages);
    messageTextEdit = (EditText) findViewById(R.id.editTextMessage);

    messageAdapter = new MessageAdapter(this);
    messagesListView.setAdapter(messageAdapter);

    setUsernameTextView();

    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        promptLogout();
      }
    });

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_chat, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_leave_channel) {
      System.out.println("Leave channel");
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void sendMessage() {
    String messageText = getTextInput();
    if (messageText.length() == 0) {
      return;
    }
    Message message = new Message(messageText);
    messageAdapter.addMessage(message);
    clearTextInput();
  }

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void promptLogout() {
    String message = getStringResource(R.string.logout_prompt_message);
    AlertDialogHandler.displayCancellableAlertWithHandler(message, context, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        ParseUser.logOut();
        showLoginActivity();
      }
    });
  }

  private void showLoginActivity() {
    Intent launchIntent = new Intent();
    launchIntent.setClass(getApplicationContext(), LoginActivity.class);
    startActivity(launchIntent);

    finish();
  }

  private void setUsernameTextView() {
    String username = ParseUser.getCurrentUser().getUsername();
    usernameTextView.setText(username);
  }

  private String getTextInput() {
    return messageTextEdit.getText().toString();
  }

  private void clearTextInput() {
    messageTextEdit.setText("");
  }
}
