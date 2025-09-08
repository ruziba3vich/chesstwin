package com.prodonik.chesstwin.helper;

import java.time.LocalDateTime;

public class TimeUUIDGenerator {

    public static String generateTimeUUID() {
        LocalDateTime now = LocalDateTime.now();
        String timeComponent = String.format("%04d%02d%02d%02d%02d%02d%09d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond(),
                now.getNano());

        if (timeComponent.length() > 32) {
            timeComponent = timeComponent.substring(0, 32);
        }

        while (timeComponent.length() < 32) {
            timeComponent += "0";
        }

        return String.format("%s-%s-%s-%s-%s",
                timeComponent.substring(0, 8),
                timeComponent.substring(8, 12),
                timeComponent.substring(12, 16),
                timeComponent.substring(16, 20),
                timeComponent.substring(20, 32));
    }
}
