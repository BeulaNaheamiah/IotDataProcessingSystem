package com.iot.query.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public static final String OPERATOR = "OPERATOR";
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final PasswordEncoder encoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  @Value("${app.security.user.name}")
  private String username;

  @Value("${app.security.user.password}")
  private String password;

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails operator =
        User.builder()
            .username(username)
            .password(encoder.encode(password))
            .roles(OPERATOR)
            .build();

    return new InMemoryUserDetailsManager(operator);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/readings/v1/aggregate")
                    .hasRole(OPERATOR)
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(basic -> basic.authenticationEntryPoint(customAuthenticationEntryPoint));
    return http.build();
  }
}
