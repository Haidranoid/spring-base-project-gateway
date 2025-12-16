package com.springbaseproject.gateway.security;

import com.springbaseproject.gateway.security.filters.JwtAuthFilterResolution;
import com.springbaseproject.gateway.helpers.requestshandlers.CustomAccessDeniedHandler;
import com.springbaseproject.gateway.helpers.requestshandlers.CustomAuthenticationEntryPoint;
import com.springbaseproject.sharedstarter.security.JwtAuthoritiesConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthFilterResolution jwtAuthFilterResolution;
    private final CorsConfigurationSource corsConfigurationSource;

    private static final String[] WHITE_LIST_URL = {
            "/actuator/**",
            "/api/v1/**",
            "/api/v2/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthoritiesConverter converter,
            JwtDecoder decoder
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                                .accessDeniedHandler(customAccessDeniedHandler) // 403
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(decoder)
                        .jwtAuthenticationConverter(converter)
                ));
                //.addFilterAfter(jwtAuthFilterResolution, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
