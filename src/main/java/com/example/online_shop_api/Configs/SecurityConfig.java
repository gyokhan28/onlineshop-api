package com.example.online_shop_api.Configs;

import com.example.online_shop_api.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/user/register", "/employee/register", "/products/{id}", "/products/get-all").permitAll()
                        .requestMatchers("/user/basket/**", "/user/cancel-order/**", "/user/profile", "/user/profile/**", "/user/orders").hasAuthority("ROLE_USER")
                        .requestMatchers("/password/**").hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/products/**").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/orders/show/{id}").hasAnyAuthority("ROLE_USER", "ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/orders/**").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/employee/profile/**", "/employee/get-all-except-admins").hasAuthority("ROLE_EMPLOYEE")
                        .requestMatchers("/employee/get-all").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/orders/change-status").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .requestMatchers("/api/image/**").hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Enable CORS with custom configuration

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8082"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        return authBuilder.build();
    }
}