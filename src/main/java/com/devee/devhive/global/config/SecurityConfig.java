package com.devee.devhive.global.config;

import static org.springframework.security.config.Customizer.withDefaults;

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
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

  private final CorsProperties corsProperties;
  private final AppProperties appProperties;
  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final CustomUserDetailService customUserDetailService;
  private final CustomOAuth2UserService customOAuth2UserService;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .sessionManagement(sessionManagement -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .oauth2Login(oauth2Login -> oauth2Login
            .authorizationEndpoint(
                authorizationEndpoint -> authorizationEndpoint
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(oAuth2AuthorizationRequestRepository()))
            .redirectionEndpoint(
                redirectionEndpoint -> redirectionEndpoint
                    .baseUri("/oauth2/callback/*"))
            .userInfoEndpoint(
                userInfoEndPoint -> userInfoEndPoint.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler())
            .failureHandler(oAuth2AuthenticationFailureHandler()))
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/api/auth/**",
                "/api/projects/list",
                "/api/projects/{projectId}",
                "/api/rank/**",
                "/api/users/{userId}",
                "/api/users/alarms/subscribe/{userId}",
                "/api/members/users/{userId}/hive-level",
                "/api/users/{userId}/exit-num",
                "/api/members/users/{userId}/project-histories",
                "/api/users/{userId}/badges",
                "/api/users/{userId}/tech-stacks",
                "/api/users/{userId}/careers",
                "/api/projects/{projectId}/vote",
                "/api/projects/{projectId}/leader-exit",
                "/api/members/users/{userId}/projects/{projectId}",
                "/api/users/{userId}/exit-process",
                "/api/comments/projects/{projectId}",
                "/login/**",
                "/api/admin/tech-stacks",
                "/api/admin/badges",
                "/chat/**",
                "/pub/**",
                "/sub/**",
                "/oauth/**"
            ).permitAll()

            .requestMatchers(
                "/api/users/**",
                "/api/favorite/**",
                "/api/bookmark/**",
                "/api/projects/**",
                "/api/chat/**",
                "/api/comments/**",
                "/api/reply/**",
                "/api/admin/**"
            ).hasAnyRole("USER", "ADMIN")

            .anyRequest().authenticated()
        )
        .logout(withDefaults())
        // LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationProcessingFilter(),
            CustomJsonUsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  @Primary
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
    return new JwtAuthenticationProcessingFilter(tokenService, userRepository);
  }

  @Bean
  public OAuth2LoginSuccessHandler oAuth2AuthenticationSuccessHandler() {
    return new OAuth2LoginSuccessHandler(oAuth2AuthorizationRequestRepository(), appProperties, userRepository);
  }

  @Bean
  public OAuth2LoginFailureHandler oAuth2AuthenticationFailureHandler() {
    return new OAuth2LoginFailureHandler(oAuth2AuthorizationRequestRepository());
  }

  /*
   * Cors 설정
   * */
  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowedHeaders(
        Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
    corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
    corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
    corsConfig.setAllowCredentials(true);
    corsConfig.setMaxAge(corsConfig.getMaxAge());

    corsConfigSource.registerCorsConfiguration("/**", corsConfig);
    return corsConfigSource;
  }
}
