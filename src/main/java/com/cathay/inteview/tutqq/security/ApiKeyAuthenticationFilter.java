package com.cathay.inteview.tutqq.security;

import com.cathay.inteview.tutqq.entity.ApiClient;
import com.cathay.inteview.tutqq.repository.ApiClientRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiClientRepository apiClientRepository;

    public ApiKeyAuthenticationFilter(ApiClientRepository apiClientRepository) {
        this.apiClientRepository = apiClientRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                String apiKeyHash = hashApiKey(apiKey);
                ApiClient apiClient = apiClientRepository
                        .findValidApiClient(apiKeyHash, Instant.now())
                        .orElse(null);

                if (apiClient != null) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            apiClient,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // Update last used timestamp
                    apiClient.setLastUsedAt(Instant.now());
                    apiClientRepository.save(apiClient);
                }
            } catch (Exception e) {
                logger.warn("Error processing API key authentication", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String hashApiKey(String apiKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(apiKey.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
