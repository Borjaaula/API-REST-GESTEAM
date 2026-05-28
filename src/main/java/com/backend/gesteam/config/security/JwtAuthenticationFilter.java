package com.backend.gesteam.config.security;

import com.backend.gesteam.entity.User;
import com.backend.gesteam.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        try{
            String token = getTokenFromRequest(request);

            if (token == null)
            {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsernameFromToken(token);

            if (username == null || SecurityContextHolder.getContext().getAuthentication() != null)
            {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<User> userOptional = userService.getUserByName(username);
            if (userOptional.isEmpty())
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado para el token proporcionado");
                return;
            }

            SecurityUserDetails user = new SecurityUserDetails(userOptional.get());

            if (StringUtils.hasText(token) && jwtService.isTokenValid(token, user))
            {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            else
            {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido o expirado");
                return;
            }

            filterChain.doFilter(request, response);

        }
        catch (Exception ex)
        {
            logger.error("Error validando JWT", ex);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
        }
    }

    /** Extrae el token JWT del encabezado Authorization si tiene el prefijo "Bearer ". */
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(JwtTokenProvider.TOKEN_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
            return authHeader.substring(JwtTokenProvider.TOKEN_PREFIX.length());
        }

        return null;
    }
}

