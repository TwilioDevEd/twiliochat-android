package com.twilio.twiliochat.chat.messages;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twilio.chat.Message;
import com.twilio.twiliochat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MessageAdapter extends BaseAdapter {
  private final int TYPE_MESSAGE = 0;
  private final int TYPE_STATUS = 1;

  private List<ChatMessage> messages;
  private LayoutInflater layoutInflater;
  private TreeSet<Integer> statusMessageSet = new TreeSet<>();

  public MessageAdapter(Activity activity) {
    layoutInflater = activity.getLayoutInflater();
    messages = new ArrayList<>();
  }

  public void setMessages(List<Message> messages) {
    this.messages = convertTwilioMessages(messages);
    this.statusMessageSet.clear();
    notifyDataSetChanged();
  }

  public void addMessage(Message message) {
    messages.add(new UserMessage(message));
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

  private List<ChatMessage> convertTwilioMessages(List<Message> messages) {
    List<ChatMessage> chatMessages = new ArrayList<>();
    for (Message message : messages) {
      chatMessages.add(new UserMessage(message));
    }
    return chatMessages;
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
        ChatMessage message = messages.get(position);
        TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
        TextView textViewAuthor = (TextView) convertView.findViewById(R.id.textViewAuthor);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        textViewMessage.setText(message.getMessageBody());
        textViewAuthor.setText(message.getAuthor());
        textViewDate.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));
        break;
      case TYPE_STATUS:
        res = R.layout.status_message;
        convertView = layoutInflater.inflate(res, viewGroup, false);
        ChatMessage status = messages.get(position);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatusMessage);
        String statusMessage = status.getMessageBody();
        textViewStatus.setText(statusMessage);
        break;
    }
    return convertView;
  }
}
