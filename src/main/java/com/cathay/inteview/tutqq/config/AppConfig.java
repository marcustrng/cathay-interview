package com.cathay.inteview.tutqq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(30_000);

        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory)) // ✅ allow multiple reads
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("➡️ Request: {} {}", request.getMethod(), request.getURI());
            request.getHeaders().forEach((name, values) ->
                    values.forEach(value -> log.info("Request Header: {}={}", name, value)));
            if (body.length > 0) {
                log.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
            }

            ClientHttpResponse response = execution.execute(request, body);

            log.info("⬅️ Response Status: {} {}", response.getStatusCode(), response.getStatusText());
            response.getHeaders().forEach((name, values) ->
                    values.forEach(value -> log.info("Response Header: {}={}", name, value)));

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                log.info("Response Body: {}", sb.toString());
            } catch (IOException e) {
                log.warn("Could not read response body", e);
            }

            return response;
        };
    }

}
