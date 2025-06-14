package com.skpijtk.springboot_boilerplate.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skpijtk.springboot_boilerplate.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component 
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session); // Tambahkan sesi baru saat ada koneksi
        logger.info("New WebSocket connection established: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session); // Hapus sesi saat koneksi ditutup
        logger.info("WebSocket connection closed: {}. Status: {}", session.getId(), status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Bisa dibiarkan kosong karena server hanya mengirim notifikasi (bukan menerima pesan)
        logger.info("Received message from {}: {}", session.getId(), message.getPayload());
    }

    /**
     * Method untuk menyiarkan (broadcast) notifikasi ke semua sesi yang terhubung.
     * @param notification Objek notifikasi yang akan dikirim.
     */
    public void broadcastNotification(NotificationDto notification) {
        try {
            // Ubah objek DTO menjadi string JSON
            String messagePayload = objectMapper.writeValueAsString(notification);
            TextMessage message = new TextMessage(messagePayload);
            
            logger.info("Broadcasting notification to {} sessions: {}", sessions.size(), messagePayload);
            
            // Lakukan loop dan kirim pesan ke setiap sesi
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            }
        } catch (IOException e) {
            logger.error("Error broadcasting notification: {}", e.getMessage(), e);
        }
    }
}