package com.example.mentormentee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration defining authorization rules and password encoder.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RoleAuthenticationSuccessHandler successHandler;

    public SecurityConfig(RoleAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/mentor/signup",
                    "/mentor/login",
                    "/mentee/signup",
                    "/mentee/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error",
                    "/403",
                    "/404"
                ).permitAll()
                .requestMatchers("/mentor/**").hasAuthority("ROLE_MENTOR")
                .requestMatchers("/mentee/**").hasAuthority("ROLE_MENTEE")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/mentor/login") // Default login fallback
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureHandler((request, response, exception) -> {
                    String loginType = request.getParameter("loginType");
                    if ("mentee".equalsIgnoreCase(loginType)) {
                        response.sendRedirect(request.getContextPath() + "/mentee/login?error=true");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/mentor/login?error=true");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }
}
