package com.backend.gesteam.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDenied implements AccessDeniedHandler
{
    private final Logger logger = LoggerFactory.getLogger(JwtAccessDenied.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException
    {
        //Se ejecuta cuando no está autorizado mediante roles.
        logger.error("Unauthorized error: {}", accessDeniedException.getMessage());

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error: No tienes permisos para acceder a este recurso");
    }
}
