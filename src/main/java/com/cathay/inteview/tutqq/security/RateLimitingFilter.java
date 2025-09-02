package com.cathay.inteview.tutqq.security;

import com.cathay.inteview.tutqq.entity.ApiClient;
import com.cathay.inteview.tutqq.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, RateLimitBucket> rateLimitBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof ApiClient) {
            ApiClient apiClient = (ApiClient) auth.getPrincipal();

            String clientKey = apiClient.getClientId().toString();

            if (!isRequestAllowed(clientKey, apiClient.getRateLimitPerHour())) {
                throw new RateLimitExceededException(
                        "API rate limit exceeded",
                        apiClient.getRateLimitPerHour(),
                        3600 // 1 hour in seconds
                );
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRequestAllowed(String clientKey, int maxRequestsPerHour) {
        Instant now = Instant.now();
        Instant hourStart = now.truncatedTo(ChronoUnit.HOURS);

        RateLimitBucket bucket = rateLimitBuckets.computeIfAbsent(
                clientKey + "_" + hourStart,
                k -> new RateLimitBucket(hourStart, maxRequestsPerHour)
        );

        rateLimitBuckets.entrySet().removeIf(entry ->
                entry.getValue().getHourStart().isBefore(now.minus(2, ChronoUnit.HOURS))
        );

        return bucket.tryConsume();
    }

    private static class RateLimitBucket {
        @Getter
        private final Instant hourStart;
        private final int maxRequests;
        private final AtomicInteger currentRequests = new AtomicInteger(0);

        public RateLimitBucket(Instant hourStart, int maxRequests) {
            this.hourStart = hourStart;
            this.maxRequests = maxRequests;
        }

        public boolean tryConsume() {
            return currentRequests.incrementAndGet() <= maxRequests;
        }
    }
}
