package com.trustai.common_base.security.filter;

import com.trustai.common_base.security.jwt.JwtProvider;
import com.trustai.common_base.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;


    public JwtAuthenticationFilter(JwtProvider jwtProvider, CustomUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);
            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.extractUsername(token);
                var userDetails = userDetailsService.loadUserByUsername(username);

                //var authentication = SecurityUtils.createAuthentication(userDetails);
                //SecurityContextHolder.getContext().setAuthentication(authentication);

                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            // skip setting security context — AuthEntryPoint will handle unauthorized responses
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest req) {
        String bearerHeader = req.getHeader("Authorization");
        if (StringUtils.hasText(bearerHeader) && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only filter /api/** (stateless API)
        String path = request.getServletPath();
        return !path.startsWith("/api/");
//        return path.startsWith("/api/v1/auth/");
    }
}
