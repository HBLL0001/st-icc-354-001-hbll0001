package com.example.practica2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfigSecurity {

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/usuario/**").hasRole("ADMIN")
                      .requestMatchers("/public/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                      .requestMatchers("/login", "/", "/error", "/process-login").permitAll()
                      .anyRequest().authenticated())
              .formLogin(form -> form
                      .loginPage("/login")
                      .loginProcessingUrl("/login")
                      .usernameParameter("username")
                      .passwordParameter("password")
                      .defaultSuccessUrl("/mockycrud/", true)
                      .failureUrl("/login?error=true")
                      .permitAll())
              .logout(logout -> logout
                      .logoutUrl("/logout")
                      .logoutSuccessUrl("/login?logout")
                      .invalidateHttpSession(true)
                      .clearAuthentication(true)
                      .deleteCookies("JSESSIONID")
                      .permitAll())
              .sessionManagement(session -> session
                      .maximumSessions(1)
                      .expiredUrl("/login?expired"))
              .exceptionHandling(exception -> exception
                      .accessDeniedPage("/403"))
              .csrf(csrf -> csrf.disable());
      return http.build();
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}