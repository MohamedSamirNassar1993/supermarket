package com.supermarket.shared.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DocumentNumberGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicLong sequence = new AtomicLong(System.currentTimeMillis() % 100000);

    public String next(String prefix) {
        return prefix + "-" + LocalDate.now().format(DATE_FMT) + "-" + sequence.incrementAndGet();
    }
}
