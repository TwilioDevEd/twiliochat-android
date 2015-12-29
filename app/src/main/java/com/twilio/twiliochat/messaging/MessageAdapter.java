package com.twilio.twiliochat.messaging;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twilio.ipmessaging.Message;
import com.twilio.twiliochat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
  private List<Message> messages;
  private LayoutInflater layoutInflater;

  public MessageAdapter(Activity activity) {
    layoutInflater = activity.getLayoutInflater();
    messages = new ArrayList<>();
  }

  public void setMessages(Message[] messages) {
    this.messages = new ArrayList<>(Arrays.asList(messages));
    notifyDataSetChanged();
  }

  public void addMessage(Message message) {
    messages.add(message);
    notifyDataSetChanged();
  }

  public void removeMessage(Message message) {
    messages.remove(messages.indexOf(message));
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return messages.size();
  }

  @Override
  public Object getItem(int i) {
    return messages.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View convertView, ViewGroup viewGroup) {
    if (convertView == null) {
      int res = R.layout.message;
      convertView = layoutInflater.inflate(res, viewGroup, false);
    }
    Message message = messages.get(i);
    TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
    TextView textViewAuthor = (TextView) convertView.findViewById(R.id.textViewAuthor);
    TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
    textViewMessage.setText(message.getMessageBody());
    textViewAuthor.setText(message.getAuthor());
    textViewDate.setText(message.getTimeStamp());
    return convertView;
  }
}