package com.example.mentormentee.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authentication success handler that redirects users to their appropriate role-based dashboard
 * and prevents role cross-login attempts.
 */
@Component
public class RoleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String requestUri = request.getRequestURI();
        String loginType = request.getParameter("loginType"); // passed via hidden input in login forms

        boolean isMentor = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_MENTOR"::equals);

        boolean isMentee = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_MENTEE"::equals);

        // Check if mentor form was used
        if ("mentor".equalsIgnoreCase(loginType) || requestUri.contains("/mentor/login")) {
            if (isMentor) {
                response.sendRedirect(request.getContextPath() + "/mentor/dashboard");
                return;
            } else {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/mentor/login?error=not_a_mentor");
                return;
            }
        }

        // Check if mentee form was used
        if ("mentee".equalsIgnoreCase(loginType) || requestUri.contains("/mentee/login")) {
            if (isMentee) {
                response.sendRedirect(request.getContextPath() + "/mentee/dashboard");
                return;
            } else {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/mentee/login?error=not_a_mentee");
                return;
            }
        }

        // Fallback default role redirect
        if (isMentor) {
            response.sendRedirect(request.getContextPath() + "/mentor/dashboard");
        } else if (isMentee) {
            response.sendRedirect(request.getContextPath() + "/mentee/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
