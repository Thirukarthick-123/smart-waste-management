package com.example.smartwaste.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Extracts the JWT from the {@code Authorization: Bearer …} header,
 * validates it and, if it is valid, populates the Spring Security
 * {@link SecurityContextHolder} with an
 * {@link UsernamePasswordAuthenticationToken}.
 *
 * This filter **does not** need an
 * {@link org.springframework.security.authentication.AuthenticationManager},
 * therefore it no longer participates in a circular dependency with
 * {@link SecurityConfig}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // only dependency we really need

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                final String username = jwtUtil.extractUsername(token);
                final String role = jwtUtil.extractRole(token); // value is like "ROLE_ADMIN"

                // Build an authentication object and put it into the context
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(role)));

                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // Invalid token → reject the request immediately
                log.warn("Invalid JWT token supplied");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        }

        // No token or token is valid – continue the filter chain
        filterChain.doFilter(request, response);
    }
}
