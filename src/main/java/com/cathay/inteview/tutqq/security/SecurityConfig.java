package com.cathay.inteview.tutqq.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityConfig {

    /**
     * Configures Spring Security to ignore certain paths from authentication.
     * <p>
     * This is typically used for static resources and API documentation endpoints
     * such as Swagger/OpenAPI. Requests matching these paths will bypass
     * Spring Security filters.
     * </p>
     * <p>
     * Ignored paths include:
     * <ul>
     *     <li>/v3/api-docs/** - OpenAPI JSON endpoints</li>
     *     <li>/swagger-ui/** - Swagger UI resources</li>
     *     <li>/swagger-ui.html - Swagger UI landing page</li>
     *     <li>/swagger-resources/** - Swagger configuration resources</li>
     *     <li>/webjars/** - WebJar static resources (JS/CSS for Swagger UI)</li>
     *     <li>** - Optionally ignores all other requests if needed</li>
     * </ul>
     * </p>
     *
     * @return a {@link WebSecurityCustomizer} that excludes the above paths from security filters
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "**"
                );
    }
}
