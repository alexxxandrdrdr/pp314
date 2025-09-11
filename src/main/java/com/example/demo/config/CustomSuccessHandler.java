package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        if ((roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getAuthority())))) {
            response.sendRedirect("/admin");
        } else if ((roles.stream().anyMatch(r -> "ROLE_USER".equals(r.getAuthority())))) {
            response.sendRedirect("/user");
        }
    }
}
