package com.smartpulse.demo.config;

import com.smartpulse.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NOTE: plus de @Component ici — on créera le bean explicitement dans SecurityConfig
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        logger.info("***** TEST : JwtAuthenticationFilter instancié *****");
    }

    @Bean
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // (garde le code que tu as déjà — il est correct)
        try {
            final String authHeader = request.getHeader("Authorization");
//            logger.debug("AuthHeader reçu : {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                logger.debug("Pas de Bearer Token – on passe au filtre suivant");
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7).trim();
//            logger.debug("Token extrait (truncated 50): {}", jwt.length() > 50 ? jwt.substring(0,50) + "..." : jwt);

            final String userMail = jwtService.extractUsername(jwt);
//            logger.debug("Mail extrait du token : {}", userMail);

            if (userMail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userMail);
//                logger.debug("Utilisateur chargé depuis BD : {}", userDetails != null ? userDetails.getUsername() : "NULL");

                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    logger.debug("Token VALID pour {}", userMail);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.info("Token INVALID pour {}", userMail);
                }
            } else {
                logger.debug("Mail NULL ou déjà authentifié – on skip");
            }
        } catch (Exception ex) {
            logger.error("Erreur dans JwtAuthenticationFilter: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}
