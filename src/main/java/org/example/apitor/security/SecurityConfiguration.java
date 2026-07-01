package org.example.apitor.security;

import org.example.apitor.security.tracker.TrackerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;
    private final TrackerFilter trackerFilter;
    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter, TrackerFilter trackerFilter) {
        this.jwtAuthFilter=jwtAuthFilter;
        this.trackerFilter=trackerFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain trackerFilterChain(HttpSecurity http) {
        http
                .securityMatcher("/aggregator/**")
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth-> auth.anyRequest().permitAll())
                .addFilterBefore(trackerFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/public/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session->
                        session.sessionCreationPolicy(
                              SessionCreationPolicy.STATELESS
                        )
                )
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
