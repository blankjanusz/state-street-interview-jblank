package com.statestreet.carrental.util;

import java.time.LocalDateTime;

public class LocalDateTimeUtil {

    public static boolean periodsOverlap(LocalDateTime range1start, LocalDateTime range1end,
                                         LocalDateTime range2start, LocalDateTime range2end) {

        return !(range1end.isBefore(range2start) || range1start.isAfter(range2end));
    }

}
