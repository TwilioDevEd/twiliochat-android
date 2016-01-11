package com.twilio.twiliochat.messaging;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twilio.ipmessaging.Message;
import com.twilio.twiliochat.R;
import com.twilio.twiliochat.util.DateFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class MessageAdapter extends BaseAdapter {
  private final int TYPE_MESSAGE = 0;
  private final int TYPE_STATUS = 1;

  private List<Message> messages;
  private LayoutInflater layoutInflater;
  private TreeSet statusMessageSet = new TreeSet();

  public MessageAdapter(Activity activity) {
    layoutInflater = activity.getLayoutInflater();
    messages = new ArrayList<>();
  }

  public void setMessages(Message[] messages) {
    this.messages = new ArrayList<>(Arrays.asList(messages));
    this.statusMessageSet.clear();
    notifyDataSetChanged();
  }

  public void addMessage(Message message) {
    messages.add(message);
    notifyDataSetChanged();
  }

  public void addStatusMessage(StatusMessage message) {
    messages.add(message);
    statusMessageSet.add(messages.size() - 1);
    notifyDataSetChanged();
  }

  public void removeMessage(Message message) {
    messages.remove(messages.indexOf(message));
    notifyDataSetChanged();
  }

  @Override
  public int getViewTypeCount() {
    return 2;
  }

  @Override
  public int getItemViewType(int position) {
    return statusMessageSet.contains(position) ? TYPE_STATUS : TYPE_MESSAGE;
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
  public View getView(int position, View convertView, ViewGroup viewGroup) {
    int type = getItemViewType(position);
    int res;
    switch (type) {
      case TYPE_MESSAGE:
        res = R.layout.message;
        convertView = layoutInflater.inflate(res, viewGroup, false);
        Message message = messages.get(position);
        TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
        TextView textViewAuthor = (TextView) convertView.findViewById(R.id.textViewAuthor);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        textViewMessage.setText(message.getMessageBody());
        textViewAuthor.setText(message.getAuthor());
        textViewDate.setText(DateFormatter.getFormattedDateFromISOString(message.getTimeStamp()));
        break;
      case TYPE_STATUS:
        res = R.layout.status_message;
        convertView = layoutInflater.inflate(res, viewGroup, false);
        StatusMessage status = (StatusMessage) messages.get(position);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatusMessage);
        String statusMessage = status.getAuthor() + " " + status.getMessageBody() + " the channel";
        textViewStatus.setText(statusMessage);
        break;
    }
    return convertView;
  }
}