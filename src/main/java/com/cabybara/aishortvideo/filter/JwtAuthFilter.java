package com.cabybara.aishortvideo.filter;

import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.service.interfaces.JwtServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtServiceInterface jwtServiceInterface;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtServiceInterface jwtServiceInterface) {
        this.userDetailsService = userDetailsService;
        this.jwtServiceInterface = jwtServiceInterface;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                logger.debug("AuthToken: {}", token);
                email = jwtServiceInterface.extractEmail(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtServiceInterface.validateToken(token, userDetails) && !jwtServiceInterface.isTokenBacklisted(token)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            logger.error("JWT expired: {}", ex.getMessage());
            sendUnauthorizedResponse(response, request.getServletPath(), "JWT token expired");
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Invalid JWT: {}", ex.getMessage());
            sendUnauthorizedResponse(response, request.getServletPath(), "Invalid JWT token");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String path, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(path)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .error("Unauthorized")
                .timestamp(new Date())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
