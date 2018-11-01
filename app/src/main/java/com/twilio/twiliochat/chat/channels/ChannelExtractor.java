package com.twilio.twiliochat.chat.channels;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;
import com.twilio.twiliochat.chat.listeners.TaskCompletionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelExtractor {

  public void extractAndSortFromChannelDescriptor(Paginator<ChannelDescriptor> paginator,
                                                  final TaskCompletionListener<List<Channel>, String> listener) {

    extractFromChannelDescriptor(paginator, new TaskCompletionListener<List<Channel>, String>() {
      @Override
      public void onSuccess(List<Channel> channels) {
        Collections.sort(channels, new CustomChannelComparator());
        listener.onSuccess(channels);
      }

      @Override
      public void onError(String s) {
        listener.onError(s);
      }
    });
  }

  private void extractFromChannelDescriptor(Paginator<ChannelDescriptor> paginator,
                                            final TaskCompletionListener<List<Channel>, String> listener) {

    final List<Channel> channels = new ArrayList<>();
    final AtomicInteger channelDescriptorCount = new AtomicInteger(paginator.getItems().size());
    for (ChannelDescriptor channelDescriptor : paginator.getItems()) {
      channelDescriptor.getChannel(new CallbackListener<Channel>() {
        @Override
        public void onSuccess(Channel channel) {
          channels.add(channel);
          int channelDescriptorsLeft = channelDescriptorCount.decrementAndGet();
          if(channelDescriptorsLeft == 0) {
            listener.onSuccess(channels);
          }
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
          listener.onError(errorInfo.getMessage());
        }
      });
    }
  }
}
