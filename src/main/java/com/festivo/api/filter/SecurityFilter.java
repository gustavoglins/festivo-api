package com.festivo.api.filter;

import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path.equals("/api/auth/login") || path.equals("/api/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = this.recoverToken(request);

        if (token != null) {
            String subjectLogin = tokenService.validateToken(token);
            if (subjectLogin != null) {
                UserDetails user = userRepository.findByEmail(subjectLogin);
                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("User authenticated: {}", user.getUsername());
                } else {
                    log.error("User not found for the login: " + subjectLogin);
                    throw new UsernameNotFoundException("User not found for the login: " + subjectLogin);
                }
            } else {
                log.error("Invalid token detected. The token could not be validated.");
                throw new BadCredentialsException("Invalid token detected. The token could not be validated.");
            }
        } else {
            log.error("No token provided or token is missing. Authentication cannot proceed.");
            throw new IllegalArgumentException("No token provided. Authentication cannot proceed.");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
