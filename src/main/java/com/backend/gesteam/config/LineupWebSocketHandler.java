package com.backend.gesteam.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Handler del WebSocket raw (/ws-lineup) que mantiene un registro de todas las sesiones
 * activas y difunde mensajes JSON a todos los clientes conectados.
 * Se usa para notificaciones en tiempo real: cambios de estado de partido,
 * actualización de estadísticas, cambios de equipo, etc.
 */
@Component
public class LineupWebSocketHandler extends TextWebSocketHandler {

    // Conjunto thread-safe de sesiones WebSocket activas
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Los clientes también pueden enviar mensajes que se reenvían a todos (broadcast)
        broadcast(message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /** Envía el JSON a todas las sesiones abiertas. */
    public void broadcast(String jsonPayload) {
        TextMessage msg = new TextMessage(jsonPayload);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try { s.sendMessage(msg); }
                catch (Exception ignored) {}
            }
        }
    }
}
