package com.statestreet.carrental.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalDateTimeUtilTest {

    @Test
    public void periodsOverlapTest_partialOverlap() {

        LocalDateTime range1start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime range1end = LocalDateTime.of(2025, 1, 2, 0, 0, 0);

        LocalDateTime range2start = LocalDateTime.of(2025, 1, 1, 3, 0, 0);
        LocalDateTime range2end = LocalDateTime.of(2025, 1, 2, 3, 0, 0);

        assertTrue(LocalDateTimeUtil.periodsOverlap(range1start, range1end, range2start, range2end));
        assertTrue(LocalDateTimeUtil.periodsOverlap(range2start, range2end, range1start, range1end));
    }

    @Test
    public void periodsOverlapTest_fullOverlap() {

        LocalDateTime range1start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime range1end = LocalDateTime.of(2025, 1, 2, 0, 0, 0);

        LocalDateTime range2start = LocalDateTime.of(2025, 1, 1, 3, 0, 0);
        LocalDateTime range2end = LocalDateTime.of(2025, 1, 1, 5, 0, 0);

        assertTrue(LocalDateTimeUtil.periodsOverlap(range1start, range1end, range2start, range2end));
        assertTrue(LocalDateTimeUtil.periodsOverlap(range2start, range2end, range1start, range1end));
    }

    @Test
    public void periodsOverlapTest_noOverlap() {

        LocalDateTime range1start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime range1end = LocalDateTime.of(2025, 1, 2, 0, 0, 0);

        LocalDateTime range2start = LocalDateTime.of(2025, 1, 3, 3, 0, 0);
        LocalDateTime range2end = LocalDateTime.of(2025, 1, 4, 5, 0, 0);

        assertFalse(LocalDateTimeUtil.periodsOverlap(range1start, range1end, range2start, range2end));
        assertFalse(LocalDateTimeUtil.periodsOverlap(range2start, range2end, range1start, range1end));
    }

}

