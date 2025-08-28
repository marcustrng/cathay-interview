package com.cathay.inteview.tutqq.exception;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DateRangeExceededException extends RuntimeException {
    // Getters
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int requestedDays;
    private final int maxDays;
    private final int maxMonths;

    public DateRangeExceededException(String message, LocalDate startDate, LocalDate endDate,
                                      int requestedDays, int maxDays, int maxMonths) {
        super(message);
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestedDays = requestedDays;
        this.maxDays = maxDays;
        this.maxMonths = maxMonths;
    }

}
