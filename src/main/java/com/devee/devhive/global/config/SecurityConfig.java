package com.devee.devhive.global.config;

import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.devee.devhive.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.devee.devhive.global.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.devee.devhive.global.oauth2.service.CustomOAuth2UserService;
import com.devee.devhive.global.security.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.devee.devhive.global.security.filter.JwtAuthenticationProcessingFilter;
import com.devee.devhive.global.security.handler.LoginFailureHandler;
import com.devee.devhive.global.security.handler.LoginSuccessHandler;
import com.devee.devhive.global.security.service.CustomUserDetailService;
import com.devee.devhive.global.security.service.TokenService;
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
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final CustomUserDetailService customUserDetailService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .authorizeHttpRequests((authorizeRequests) -> {
          authorizeRequests.requestMatchers(
              "/api/auth/**",
              "/api/nonusers/**"
          ).permitAll();
          authorizeRequests.requestMatchers(
              "/api/users/**",
              "/api/favorite/**",
              "/api/bookmark/**",
              "/api/projects/**",
              "/api/chat/**",
              "/api/comments/**",
              "/api/reply/**"
          ).hasAnyRole("USER", "ADMIN");

          authorizeRequests.requestMatchers(
              "/api/admin/**"
          ).hasRole("ADMIN");
        })
        .logout(logout -> logout.logoutSuccessUrl("/"))
        .oauth2Login(oauth2Login -> oauth2Login
            .authorizationEndpoint(
                authorizationEndpoint -> authorizationEndpoint
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(oAuth2AuthorizationRequestRepository()))
            .redirectionEndpoint(
                redirectionEndpoint -> redirectionEndpoint.baseUri("/api/oauth2/code/*"))
            .userInfoEndpoint(
                userInfoEndPoint -> userInfoEndPoint.userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler)
            .failureHandler(oAuth2LoginFailureHandler))
        // LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationProcessingFilter(),
            CustomJsonUsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
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
    return new LoginSuccessHandler(tokenService, userRepository);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter
      () {
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
        tokenService, userRepository);
  }
}
