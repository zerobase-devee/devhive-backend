package com.devee.devhive.global.config;

import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.security.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.devee.devhive.global.security.filter.JwtAuthenticationProcessingFilter;
import com.devee.devhive.global.security.handler.LoginFailureHandler;
import com.devee.devhive.global.security.handler.LoginSuccessHandler;
import com.devee.devhive.global.security.service.CustomUserDetailService;
import com.devee.devhive.global.security.service.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final CustomUserDetailService customUserDetailService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(frameOptions -> headers.disable()))
//        .exceptionHandling(exceptionHandling ->
//            exceptionHandling.accessDeniedHandler(tokenAccessDeniedHandler)
//            .authenticationEntryPoint(new RestAuthenticationEntryPoint()
//            )
//        )
        .authorizeHttpRequests((authorizeRequests) -> {
          authorizeRequests.requestMatchers("/api/user/**").permitAll();

          authorizeRequests.requestMatchers("/api/admin/test").hasRole("ADMIN");

          authorizeRequests.anyRequest().permitAll();
        })
        .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationProcessingFilter(),
            CustomJsonUsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(customUserDetailService);
    return new ProviderManager(provider);
  }

  @Bean
  public LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler(tokenProvider, userRepository);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
    CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
        = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
    customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
    customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
    customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
    return customJsonUsernamePasswordLoginFilter;
  }

  @Bean
  public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
    return new JwtAuthenticationProcessingFilter(
        tokenProvider, userRepository);
  }
}
