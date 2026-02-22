package com.example.smartwaste.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.smartwaste.security.JwtAuthenticationFilter;
import com.example.smartwaste.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Central Spring‑Security configuration.
 *
 * <ul>
 * <li>GET /api/** → public (no token required)</li>
 * <li>POST /api/auth/** → public (login / logout)</li>
 * <li>All other requests → must contain a valid JWT with ROLE_ADMIN</li>
 * </ul>
 *
 * The {@link JwtAuthenticationFilter} validates the token and populates the
 * {@link org.springframework.security.core.context.SecurityContextHolder}.
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /* ------------------------------------------------------------------- */
    /* Password encoder – BCrypt (the default for Spring Security) */
    /* ------------------------------------------------------------------- */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ------------------------------------------------------------------- */
    /* AuthenticationManager – wires the UserDetailsService & encoder */
    /* ------------------------------------------------------------------- */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    /* ------------------------------------------------------------------- */
    /* HTTP security – URL authorisation & JWT filter registration */
    /* ------------------------------------------------------------------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1️⃣ CORS & CSRF
                .cors(cors -> cors.configurationSource(request -> {
                    var cfg = new org.springframework.web.cors.CorsConfiguration();
                    cfg.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://127.0.0.1:3000"));
                    cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(java.util.List.of("*"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .csrf(csrf -> csrf.disable())

                // 2️⃣ No HTTP session (JWT is stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3️⃣ URL authorisation rules
                .authorizeHttpRequests(auth -> auth
                        // Public telemetry endpoint for hardware
                        .requestMatchers(HttpMethod.POST, "/api/bins/telemetry").permitAll()
                        // Public bin, route and alert endpoints
                        .requestMatchers(HttpMethod.GET, "/api/bins").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/route/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/alerts").permitAll()
                        // Public login / logout endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        // Anything else needs a valid JWT token
                        .anyRequest().authenticated())

                // 4️⃣ Insert our JWT filter *before* the default username‑password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 5️⃣ Return 401 (Unauthorized) when authentication fails
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (request, response, authException) -> response
                                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));

        return http.build();
    }
}
