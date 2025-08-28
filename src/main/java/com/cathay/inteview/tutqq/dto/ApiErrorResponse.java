package com.cathay.inteview.tutqq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorResponse {
    private String code;       // e.g. CURRENCY_NOT_FOUND
    private String message;    // localized message
    private String path;       // request path
    private Instant timestamp; // error time
}