package com.photory.common.util;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class DateUtil {

    public static long convertToTimeInterval(LocalDateTime createdTime) {

        Timestamp timestamp = Timestamp.valueOf(createdTime);
        long createdTimeInterval = timestamp.getTime() / 1000L;

        return createdTimeInterval;
    }
}