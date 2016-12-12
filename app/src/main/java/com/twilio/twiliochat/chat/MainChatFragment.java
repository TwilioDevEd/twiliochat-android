package com.twilio.twiliochat.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;
import com.twilio.chat.StatusListener;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.chat.messages.JoinedStatusMessage;
import com.twilio.twiliochat.chat.messages.LeftStatusMessage;
import com.twilio.twiliochat.chat.messages.MessageAdapter;
import com.twilio.twiliochat.chat.messages.StatusMessage;

import java.util.List;

public class MainChatFragment extends Fragment implements ChannelListener {
  Context context;
  Activity mainActivity;
  Button sendButton;
  ListView messagesListView;
  EditText messageTextEdit;

  MessageAdapter messageAdapter;
  Channel currentChannel;
  Messages messagesObject;

  public MainChatFragment() {
  }

  public static MainChatFragment newInstance() {
    MainChatFragment fragment = new MainChatFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this.getActivity();
    mainActivity = this.getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main_chat, container, false);
    sendButton = (Button) view.findViewById(R.id.buttonSend);
    messagesListView = (ListView) view.findViewById(R.id.listViewMessages);
    messageTextEdit = (EditText) view.findViewById(R.id.editTextMessage);

    messageAdapter = new MessageAdapter(mainActivity);
    messagesListView.setAdapter(messageAdapter);
    setUpListeners();
    setMessageInputEnabled(false);

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  public Channel getCurrentChannel() {
    return currentChannel;
  }

  public void setCurrentChannel(Channel currentChannel, final StatusListener handler) {
    if (currentChannel == null) {
      this.currentChannel = null;
      return;
    }
    if (!currentChannel.equals(this.currentChannel)) {
      setMessageInputEnabled(false);
      this.currentChannel = currentChannel;
      this.currentChannel.addListener(this);
      if (this.currentChannel.getStatus() == Channel.ChannelStatus.JOINED) {
        loadMessages(handler);
      } else {
        this.currentChannel.join(new StatusListener() {
          @Override
          public void onSuccess() {
            loadMessages(handler);
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
          }
        });
      }
    }
  }

  private void setUpListeners() {
    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
    messageTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
      }
    });
  }

  private void sendMessage() {
    String messageText = getTextInput();
    if (messageText.length() == 0) {
      return;
    }
    Message newMessage = this.messagesObject.createMessage(messageText);
    this.messagesObject.sendMessage(newMessage, null);
    clearTextInput();
  }

  private void loadMessages(final StatusListener handler) {
    this.messagesObject = this.currentChannel.getMessages();

    if (messagesObject != null) {
      messagesObject.getLastMessages(100, new CallbackListener<List<Message>>() {
        @Override
        public void onSuccess(List<Message> messageList) {
          messageAdapter.setMessages(messageList);
          setMessageInputEnabled(true);
          messageTextEdit.requestFocus();
          handler.onSuccess();
        }
      });
    }
  }

  private void setMessageInputEnabled(final boolean enabled) {
    mainActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        MainChatFragment.this.sendButton.setEnabled(enabled);
        MainChatFragment.this.messageTextEdit.setEnabled(enabled);
      }
    });
  }

  private String getTextInput() {
    return messageTextEdit.getText().toString();
  }

  private void clearTextInput() {
    messageTextEdit.setText("");
  }

  @Override
  public void onMessageAdd(Message message) {
    messageAdapter.addMessage(message);
  }

  @Override
  public void onMemberJoin(Member member) {
    StatusMessage statusMessage = new JoinedStatusMessage(member.getUserInfo().getIdentity());
    this.messageAdapter.addStatusMessage(statusMessage);
  }

  @Override
  public void onMemberDelete(Member member) {
    StatusMessage statusMessage = new LeftStatusMessage(member.getUserInfo().getIdentity());
    this.messageAdapter.addStatusMessage(statusMessage);
  }

  @Override
  public void onMessageChange(Message message) {
  }

  @Override
  public void onMessageDelete(Message message) {
  }

  @Override
  public void onMemberChange(Member member) {
  }

  @Override
  public void onTypingStarted(Member member) {
  }

  @Override
  public void onTypingEnded(Member member) {
  }

  @Override
  public void onSynchronizationChange(Channel channel) {
  }
}
