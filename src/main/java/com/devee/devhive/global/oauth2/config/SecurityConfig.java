package com.devee.devhive.global.oauth2.config;

import com.devee.devhive.global.oauth2.domain.type.Role;
import com.devee.devhive.global.oauth2.handler.OAuth2LoginSuccessHandler;
import com.devee.devhive.global.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .authorizeHttpRequests(requests ->
            requests.requestMatchers("/", "/css/**", "/images/**",
                    "/js/**", "/h2-console/**").permitAll()
                .requestMatchers("api/user/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()
        )
        .logout(logout -> logout.logoutSuccessUrl("/"))
        .oauth2Login(oauth2Login -> oauth2Login
            .authorizationEndpoint(
                authorizationEndpoint -> authorizationEndpoint.baseUri("/oauth2/authorize"))
            .redirectionEndpoint(
                redirectionEndpoint -> redirectionEndpoint.baseUri("/*/oauth2/code/*"))
            .userInfoEndpoint(
                userInfoEndPoint -> userInfoEndPoint.userService(customOAuth2UserService))
            .successHandler(oAuth2LoginSuccessHandler));

    return http.build();
  }
}
