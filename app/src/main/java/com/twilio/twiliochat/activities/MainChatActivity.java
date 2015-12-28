package com.twilio.twiliochat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Channels;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.fragments.MainChatFragment;
import com.twilio.twiliochat.ipmessaging.ChannelAdapter;
import com.twilio.twiliochat.ipmessaging.IPMessagingClient;
import com.twilio.twiliochat.ipmessaging.LoginListener;
import com.twilio.twiliochat.util.AlertDialogHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainChatActivity extends AppCompatActivity implements IPMessagingClientListener {
  private Context context;
  Activity mainActivity;
  private Button logoutButton;
  private TextView usernameTextView;
  private IPMessagingClient client;
  private ListView channelsListView;
  private List<Channel> channels = new ArrayList<>();
  private Channels channelsObject;
  private Channel[] channelArray;
  private ChannelAdapter channelAdapter;

  private String defaultChannelName;

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

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

    MainChatFragment fragment = new MainChatFragment();
    fragmentTransaction.add(R.id.fragment_container, fragment);
    fragmentTransaction.commit();

    context = this;
    mainActivity = this;
    logoutButton = (Button) findViewById(R.id.buttonLogout);
    usernameTextView = (TextView) findViewById(R.id.textViewUsername);

    defaultChannelName = getStringResource(R.string.default_channel_name);
    setUsernameTextView();

    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        promptLogout();
      }
    });

    checkTwilioClient();
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

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void populateChannels() {
    client.setClientListener(MainChatActivity.this);
    channelsListView = (ListView) findViewById(R.id.listViewChannels);
    channelAdapter = new ChannelAdapter(mainActivity, this.channels);
    channelsListView.setAdapter(channelAdapter);
    channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      }
    });
    getChannels();
  }

  private void getChannels() {
    if (this.client == null) {
      return;
    }

    channelsObject = client.getIpMessagingClient().getChannels();
    if (channelsObject != null) {
      channelsObject.loadChannelsWithListener(new Constants.StatusListener() {
        @Override
        public void onError() {
          System.out.println("Failed to loadChannelsWithListener");
        }

        @Override
        public void onSuccess() {
          System.out.println("Successfully loadChannelsWithListener.");
          if (channels != null) {
            channels.clear();
          }

          channelArray = channelsObject.getChannels();
          //setupListenersForChannel(channelArray);
          if (MainChatActivity.this.channels != null && channelArray != null) {
            MainChatActivity.this.channels.addAll(Arrays.asList(channelArray));
            Collections.sort(MainChatActivity.this.channels, new CustomChannelComparator());
            channelAdapter.notifyDataSetChanged();
          }
        }
      });
    }
  }

  private void setChannel(int position) {

  }

  private void checkTwilioClient() {
    client = TwilioChatApplication.get().getIPMessagingClient();
    if (client.getIpMessagingClient() == null) {
      initializeClient();
    }
    else {
      populateChannels();
    }
  }

  private void initializeClient() {
    client.connectClient(new LoginListener() {
      @Override
      public void onLoginStarted() {

      }

      @Override
      public void onLoginFinished() {
        populateChannels();
      }

      @Override
      public void onLoginError(String errorMessage) {
        AlertDialogHandler.displayAlertWithMessage("Client connection error", context);
      }

      @Override
      public void onLogoutFinished() {

      }
    });
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

  /*private void populateChannels(String channelId) {
    if (this.channels != null) {
      if (client != null && client.getIpMessagingClient() != null) {
        channelsObject = basicClient.getIpMessagingClient().getChannels();
        if (channelsObject != null) {
          channelsObject.loadChannelsWithListener(new Constants.StatusListener() {
            @Override
            public void onError() {
              logger.d("Failed to loadChannelsWithListener");
            }

            @Override
            public void onSuccess() {
              logger.d("Successfully loadChannelsWithListener.");
              if (channels != null) {
                channels.clear();
              }
              if (channelsObject != null) {
                channelArray = channelsObject.getChannels();
                setupListenersForChannel(channelArray);
                if (ChannelActivity.this.channels != null && channelArray != null) {
                  ChannelActivity.this.channels
                      .addAll(new ArrayList<Channel>(Arrays.asList(channelArray)));
                  Collections.sort(ChannelActivity.this.channels, new CustomChannelComparator());
                  adapter.notifyDataSetChanged();
                }
              }
            }
          });
        }
      }
    }
  }*/

  @Override
  public void onChannelAdd(Channel channel) {

  }

  @Override
  public void onChannelChange(Channel channel) {

  }

  @Override
  public void onChannelDelete(Channel channel) {

  }

  @Override
  public void onError(int i, String s) {

  }

  @Override
  public void onAttributesChange(String s) {

  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {

  }

  private class CustomChannelComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel lhs, Channel rhs) {
      if (lhs.getFriendlyName().contentEquals(defaultChannelName)) {
        return -100;
      }
      else if (rhs.getFriendlyName().contentEquals(defaultChannelName)) {
        return 100;
      }
      return lhs.getFriendlyName().compareTo(rhs.getFriendlyName());
    }
  }
}
