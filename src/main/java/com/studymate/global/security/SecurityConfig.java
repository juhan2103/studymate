package com.studymate.global.security;

import com.studymate.auth.handler.OAuth2FailureHandler;
import com.studymate.auth.handler.OAuth2SuccessHandler;
import com.studymate.auth.service.CustomOAuth2UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** 보안 설정: OAuth2 로그인 + 무상태 JWT 필터체인. */
@Configuration
public class SecurityConfig {

  private final CustomOAuth2UserService oAuth2UserService;
  private final OAuth2SuccessHandler successHandler;
  private final OAuth2FailureHandler failureHandler;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final List<String> allowedOrigins;

  public SecurityConfig(
      CustomOAuth2UserService oAuth2UserService,
      OAuth2SuccessHandler successHandler,
      OAuth2FailureHandler failureHandler,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      CustomAuthenticationEntryPoint authenticationEntryPoint,
      @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
    this.oAuth2UserService = oAuth2UserService;
    this.successHandler = successHandler;
    this.failureHandler = failureHandler;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.allowedOrigins = allowedOrigins;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/oauth2/**", "/login/**", "/api/auth/refresh")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/studies/**")
                    .permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth ->
                oauth
                    .userInfoEndpoint(ui -> ui.userService(oAuth2UserService))
                    .successHandler(successHandler)
                    .failureHandler(failureHandler))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
