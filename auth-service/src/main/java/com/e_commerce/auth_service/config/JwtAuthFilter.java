package com.e_commerce.auth_service.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtProvider jwtProvider;

    public JwtAuthFilter(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

            String header = request.getHeader("Authorization");

            if(header != null && header.startsWith("Baerer ")){

                String token = header.substring(7);
                Claims claims = jwtProvider.getClaims(token);

                List<SimpleGrantedAuthority> authorities =
                        ((List<String>) claims.get("roles"))
                                .stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                                .toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            }

            filterChain.doFilter(request, response);

    }
}
