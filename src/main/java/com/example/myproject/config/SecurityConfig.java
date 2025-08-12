package com.example.myproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração de segurança da aplicação.
 * 
 * Esta classe configura a segurança da aplicação Spring Boot,
 * incluindo autenticação, autorização e CORS.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Configura o filtro de segurança.
     * 
     * @param http o objeto HttpSecurity para configuração
     * @return o filtro de segurança configurado
     * @throws Exception se houver erro na configuração
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF para APIs REST
            .csrf().disable()
            
            // Configurar CORS
            .cors().configurationSource(corsConfigurationSource())
            
            .and()
            
            // Configurar sessões como stateless
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            
            // Configurar autorização de requests
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers(
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/actuator/health",
                    "/actuator/info",
                    "/h2-console/**"
                ).permitAll()
                
                // Permitir criação de usuários (registro público)
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                
                // Endpoints que requerem autenticação
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )
            
            // Configurar autenticação básica HTTP
            .httpBasic()
            
            .and()
            
            // Configurar headers de segurança
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Para H2 Console
                .contentTypeOptions().and()
                .xssProtection().and()
                .referrerPolicy().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            );

        return http.build();
    }

    /**
     * Bean para codificação de senhas.
     * 
     * @return o encoder de senhas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuração de CORS.
     * 
     * @return a configuração de CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origens permitidas
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "https://localhost:*",
            "http://127.0.0.1:*",
            "https://127.0.0.1:*"
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Headers expostos
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "Content-Disposition"
        ));
        
        // Permitir credenciais
        configuration.setAllowCredentials(true);
        
        // Tempo de cache para preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

