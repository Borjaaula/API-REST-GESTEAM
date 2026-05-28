package com.backend.gesteam.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de seguridad de Spring Security.
 * Usa sesiones sin estado (STATELESS) y autenticación mediante JWT.
 * El filtro {@link JwtAuthenticationFilter} valida el token en cada petición
 * antes de que llegue al controlador.
 * Las rutas públicas (login, registro, WebSocket, Swagger, imágenes de perfil y
 * partidos públicos) no requieren autenticación; el resto sí.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtAccessDenied jwtAccessDenied;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtEntryPoint jwtEntryPoint,
                          JwtAccessDenied jwtAccessDenied) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtEntryPoint = jwtEntryPoint;
        this.jwtAccessDenied = jwtAccessDenied;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtEntryPoint)
                        .accessDeniedHandler(jwtAccessDenied))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/*/profile-image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/matches/public").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/matches/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/matches/*/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/teams/*/profile-image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/teams/*/players").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/teams/*/summary").permitAll()
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/error",
                                // WebSocket endpoints (handshake HTTP no lleva token)
                                "/ws/**",
                                "/ws-lineup/**",
                                "/ws-lineup")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
