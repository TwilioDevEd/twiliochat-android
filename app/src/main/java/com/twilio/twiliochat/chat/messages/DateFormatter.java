package com.twilio.twiliochat.chat.messages;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateFormatter {
  private DateFormatter() {
  }

  public static DateTime getDateFromISOString(String inputDate) {
    DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTime();
    DateTime date = null;

    try {
      date = isoFormatter.parseDateTime(inputDate);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    return date;
  }

  public static String getDateTodayString(DateTime today) {
    DateTime todayMidnight = new DateTime().withTimeAtStartOfDay();
    String stringDate;
    if (todayMidnight.isEqual(today.withTimeAtStartOfDay())) {
      stringDate = "Today - ";
    } else {
      stringDate = today.toString("MMM. dd - ");
    }

    stringDate = stringDate.concat(today.toString("hh:mma"));

    return stringDate;
  }

  public static String getFormattedDateFromISOString(String inputDate) {
    return getDateTodayString(getDateFromISOString(inputDate));
  }
}
