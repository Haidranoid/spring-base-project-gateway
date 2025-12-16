package com.springbaseproject.gateway.helpers.requestshandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        logger.info("request URL: {}", request.getRequestURL());
        logger.info("Access denied: {}", HttpStatus.FORBIDDEN);

        //Set response status to 403 Unauthorized
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // Create JSON response
        String jsonResponse = new ObjectMapper().writeValueAsString(
                Map.of("error", HttpStatus.FORBIDDEN.getReasonPhrase(), "message", accessDeniedException.getMessage())
        );

        // Write the response body
        response.getWriter().write(jsonResponse);
    }
}
