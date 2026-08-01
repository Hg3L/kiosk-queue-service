package com.thh.kiosk.queue.modules.counter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CounterNameUtils {

    private static final java.util.regex.Pattern TICKET_PREFIX_PATTERN = Pattern.compile("^[A-Z]+");

    public static String generateCounterName(String ticketCode, String counterName) {
        Matcher m = TICKET_PREFIX_PATTERN.matcher(ticketCode);
        if (!m.find()) return counterName;
        char firstLetter = m.group().charAt(0);
        int counterNumber = firstLetter - 'A' + 1;
        return "Quầy " + counterNumber + " - " + counterName;
    }
}
