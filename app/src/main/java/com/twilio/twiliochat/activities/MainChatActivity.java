package com.twilio.twiliochat.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.application.TwilioChatApplication;
import com.twilio.twiliochat.fragments.MainChatFragment;
import com.twilio.twiliochat.helpers.InputOnClickListener;
import com.twilio.twiliochat.ipmessaging.ChannelAdapter;
import com.twilio.twiliochat.ipmessaging.ChannelManager;
import com.twilio.twiliochat.ipmessaging.IPMessagingClientManager;
import com.twilio.twiliochat.ipmessaging.LoadChannelListener;
import com.twilio.twiliochat.ipmessaging.LoginListener;
import com.twilio.twiliochat.util.AlertDialogHandler;

import java.util.List;

public class MainChatActivity extends AppCompatActivity implements IPMessagingClientListener {
  private Context context;
  private Activity mainActivity;
  private Button logoutButton;
  private Button addChannelButton;
  private TextView usernameTextView;
  private IPMessagingClientManager client;
  private ListView channelsListView;
  private ChannelAdapter channelAdapter;
  private ChannelManager channelManager;
  private MainChatFragment chatFragment;
  private DrawerLayout drawer;
  private ProgressDialog progressDialog;
  private MenuItem leaveChannelMenuItem;
  private MenuItem deleteChannelMenuItem;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_chat);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

    chatFragment = new MainChatFragment();
    fragmentTransaction.add(R.id.fragment_container, chatFragment);
    fragmentTransaction.commit();

    context = this;
    mainActivity = this;
    logoutButton = (Button) findViewById(R.id.buttonLogout);
    addChannelButton = (Button) findViewById(R.id.buttonAddChannel);
    usernameTextView = (TextView) findViewById(R.id.textViewUsername);
    channelManager = ChannelManager.getInstance();
    setUsernameTextView();

    setUpListeners();
    checkTwilioClient();
  }

  private void setUpListeners() {
    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        promptLogout();
      }
    });
    addChannelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAddChannelDialog();
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_chat, menu);
    this.leaveChannelMenuItem = menu.findItem(R.id.action_leave_channel);
    this.leaveChannelMenuItem.setVisible(false);
    this.deleteChannelMenuItem = menu.findItem(R.id.action_delete_channel);
    this.deleteChannelMenuItem.setVisible(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_leave_channel) {
      leaveCurrentChannel();
      return true;
    }
    if (id == R.id.action_delete_channel) {
      deleteCurrentChannel();
    }

    return super.onOptionsItemSelected(item);
  }

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void populateChannels() {
    channelManager.setChannelListener(this);
    channelManager.populateChannels(new LoadChannelListener() {
      @Override
      public void onChannelsFinishedLoading(List<Channel> channels) {
        channelsListView = (ListView) findViewById(R.id.listViewChannels);
        channelAdapter = new ChannelAdapter(mainActivity, channels);
        channelsListView.setAdapter(channelAdapter);
        channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setChannel(position);
          }
        });
        MainChatActivity.this.channelManager.joinGeneralChannelWithCompletion(new Constants.StatusListener() {
          @Override
          public void onSuccess() {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                channelAdapter.notifyDataSetChanged();
                setChannel(0);
                stopStatusDialog();
              }
            });
          }

          @Override
          public void onError() {
            System.out.println("Error joining the channel");
          }
        });
      }
    });
  }

  private void setChannel(Channel channel) {
    chatFragment.setCurrentChannel(channel);
    setTitle(channel.getFriendlyName());
    drawer.closeDrawer(GravityCompat.START);
  }

  private void setChannel(final int position) {
    List<Channel> channels = channelManager.getChannels();
    if (channels == null) {
      return;
    }
    MainChatActivity.this.leaveChannelMenuItem.setVisible(position != 0);
    MainChatActivity.this.deleteChannelMenuItem.setVisible(position != 0);
    Channel selectedChannel = channels.get(position);
    if (selectedChannel != null) {
      chatFragment.setCurrentChannel(selectedChannel);
      setTitle(selectedChannel.getFriendlyName());
      drawer.closeDrawer(GravityCompat.START);
    }
    else {
      System.out.println("Selected channel out of range");
    }
  }

  private void showAddChannelDialog() {
    String message = getStringResource(R.string.new_channel_prompt);
    AlertDialogHandler.displayInputDialog(message, context, new InputOnClickListener() {
      @Override
      public void onClick(String input) {
        if (input.length() == 0) {
          AlertDialogHandler.displayAlertWithMessage("Channel name cannot be empty", context);
          return;
        }
        createChannelWithName(input);
      }
    });
  }

  private void createChannelWithName(String name) {
    this.channelManager.createChannelWithName(name, new Constants.StatusListener() {
      @Override
      public void onSuccess() {
        populateChannels();
      }

      @Override
      public void onError() {
        AlertDialogHandler.displayAlertWithMessage("There was an error while creating the channel", context);
      }
    });
  }

  private void deleteCurrentChannel() {
    Channel currentChannel = chatFragment.getCurrentChannel();
    setChannel(0);
    channelManager.deleteChannelWithHandler(currentChannel, new Constants.StatusListener() {
      @Override
      public void onSuccess() {
        populateChannels();
      }

      @Override
      public void onError() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            AlertDialogHandler.displayAlertWithMessage("You cannot delete this channel", context);
          }
        });
      }
    });
  }

  private void leaveCurrentChannel() {
    Channel currentChannel = chatFragment.getCurrentChannel();
    setChannel(0);
    channelManager.leaveChannelWithHandler(currentChannel, new Constants.StatusListener() {
      @Override
      public void onSuccess() {

      }

      @Override
      public void onError() {

      }
    });
  }

  private void checkTwilioClient() {
    showActivityIndicator("Loading channel information");
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
    });
  }

  private void promptLogout() {
    String message = getStringResource(R.string.logout_prompt_message);
    AlertDialogHandler.displayCancellableAlertWithHandler(message, context, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        ParseUser.logOut();
        TwilioIPMessagingSDK.shutdown();
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

  private void stopStatusDialog() {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void showActivityIndicator(String message) {
    progressDialog = new ProgressDialog(this.mainActivity);
    progressDialog.setMessage(message);
    progressDialog.show();
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(false);
  }

  @Override
  public void onChannelAdd(Channel channel) {
    //this.channelAdapter.addChannel(channel);
  }

  @Override
  public void onChannelChange(Channel channel) {

  }

  @Override
  public void onChannelDelete(Channel channel) {
    //this.channelAdapter.deleteChannel(channel);
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
}
