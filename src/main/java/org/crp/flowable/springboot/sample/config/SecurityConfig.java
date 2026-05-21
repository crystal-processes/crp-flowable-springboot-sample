package org.crp.flowable.springboot.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Configuration using Flowable's native authentication.
 * Uses Spring Security's standard authentication mechanisms with Flowable as the user store.
 *
 * Default login endpoint: POST /login
 * Default logout endpoint: POST /logout
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/index.html")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/health")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/login?*")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/static/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/assets/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.js")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.css")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.svg")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.ico")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.json")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/mcp")).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(LogoutConfigurer::permitAll)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
