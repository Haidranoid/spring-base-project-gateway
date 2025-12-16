package com.springbaseproject.gateway.helpers.requestshandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        logger.info("request URL: {}", request.getRequestURL());
        logger.info("Access denied: {}", HttpStatus.UNAUTHORIZED);

        //response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: " + authException.getMessage());

        //Set response status to 401 Unauthorized
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Create JSON response
        String jsonResponse = new ObjectMapper().writeValueAsString(
                Map.of("error", HttpStatus.UNAUTHORIZED.getReasonPhrase(), "message", authException.getMessage())
        );

        // Write the response body
        response.getWriter().write(jsonResponse);
    }
}