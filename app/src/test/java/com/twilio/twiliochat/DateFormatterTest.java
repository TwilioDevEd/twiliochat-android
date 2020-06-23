package com.twilio.twiliochat;


import com.twilio.twiliochat.chat.messages.DateFormatter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateFormatterTest  {


    @Test
    public void testGetDateFromISOString() {
        String inputDate = "2015-10-21T07:28:00.000Z";

        DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTime();
        DateTime date = null;

        try {
            date = isoFormatter.parseDateTime(inputDate);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        DateTime outputDate = DateFormatter.getDateFromISOString(inputDate);

        assertEquals(outputDate, date);
    }

    @Test
    public void testGetDateTodayString() {
        DateTime today = new DateTime();

        String dateString = DateFormatter.getDateTodayString(today);

        String expectedString = "Today - " + today.toString("hh:mma");
        assertEquals(dateString, expectedString);
    }
}