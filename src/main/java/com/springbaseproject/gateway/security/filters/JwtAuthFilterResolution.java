package com.springbaseproject.gateway.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilterResolution extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilterResolution.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        var requestStatus = getString(response);
        var requestURL = request.getRequestURL().toString();

        if (requestURL.contains("actuator") || requestURL.contains("favicon")) {
            return;
        }

        logger.info(
                "[{}][{}:{}] {}",
                request.getMethod(),
                response.getStatus(),
                requestStatus,
                requestURL
        );
    }

    private static String getString(HttpServletResponse response) {
        var responseCodeStatus = response.getStatus();
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var requestStatus = "ℹ️";
        if ((responseCodeStatus >= 200) && (responseCodeStatus < 300)) {
            requestStatus = "✅";
        } else if ((responseCodeStatus >= 300) && (responseCodeStatus < 400)) {
            requestStatus = "↪️";
        } else if ((responseCodeStatus >= 400) && (responseCodeStatus < 500)) {
            requestStatus = "❌";
            if (responseCodeStatus == 401)
                requestStatus = "🔒"; // Unauthorized - 401
            if (responseCodeStatus == 403)
                requestStatus = "🚫"; // Forbidden - 403
            if (responseCodeStatus == 404)
                requestStatus = "❓"; // Not Found - 404
        } else if ((responseCodeStatus >= 500) && (responseCodeStatus < 600)) {
            requestStatus = "🔥"; // server error
        } else {
            requestStatus = "👻"; // unknown error
        }
        return requestStatus;
    }
}