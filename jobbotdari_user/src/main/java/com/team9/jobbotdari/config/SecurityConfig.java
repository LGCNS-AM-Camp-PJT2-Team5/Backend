package com.team9.jobbotdari.config;

import com.team9.jobbotdari.security.JwtAuthenticationFilter;
import com.team9.jobbotdari.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    private final String[] swaggerPath = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/error"};
    private final String[] permitPath = {"/", "/api/auth/**"};

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .cors(Customizer.withDefaults())
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .logout(auth -> auth.disable())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(permitPath).permitAll() // 인증 없이 접근 가능한 경로 설정
                    .requestMatchers(swaggerPath).permitAll() // 스웨거 경로 설정
                    //.requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated())
                .sessionManagement(auth -> auth
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        return authManagerBuilder.build();
    }
}