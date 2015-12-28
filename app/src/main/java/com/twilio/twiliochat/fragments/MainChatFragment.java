package com.twilio.twiliochat.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.ChannelListener;
import com.twilio.ipmessaging.Member;
import com.twilio.ipmessaging.Message;
import com.twilio.ipmessaging.Messages;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.messaging.MessageAdapter;
import com.twilio.twiliochat.messaging.MessageMock;

import java.util.Map;

public class MainChatFragment extends Fragment implements ChannelListener {
  Context context;
  Activity mainActivity;
  Button sendButton;
  ListView messagesListView;
  EditText messageTextEdit;

  MessageAdapter messageAdapter;
  Channel currentChannel;
  Messages messages;
  Message[] messagesArray;

  public MainChatFragment() {
  }

  public static MainChatFragment newInstance() {
    MainChatFragment fragment = new MainChatFragment();
    return fragment;
  }

  public Channel getCurrentChannel() {
    return currentChannel;
  }

  public void setCurrentChannel(Channel currentChannel) {
    this.currentChannel = currentChannel;
    this.currentChannel.setListener(this);
    this.messages = this.currentChannel.getMessages();
    messagesArray = this.messages.getMessages();
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

    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendMessage();
      }
    });
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

  private void sendMessage() {
    String messageText = getTextInput();
    if (messageText.length() == 0) {
      return;
    }
    MessageMock message = new MessageMock(messageText);
    messageAdapter.addMessage(message);
    clearTextInput();
  }

  private String getTextInput() {
    return messageTextEdit.getText().toString();
  }

  private void clearTextInput() {
    messageTextEdit.setText("");
  }

  @Override
  public void onMessageAdd(com.twilio.ipmessaging.Message message) {

  }

  @Override
  public void onMessageChange(com.twilio.ipmessaging.Message message) {

  }

  @Override
  public void onMessageDelete(com.twilio.ipmessaging.Message message) {

  }

  @Override
  public void onMemberJoin(Member member) {

  }

  @Override
  public void onMemberChange(Member member) {

  }

  @Override
  public void onMemberDelete(Member member) {

  }

  @Override
  public void onAttributesChange(Map<String, String> map) {

  }

  @Override
  public void onTypingStarted(Member member) {

  }

  @Override
  public void onTypingEnded(Member member) {

  }

  @Override
  public void onChannelHistoryLoaded(Channel channel) {

  }
}
