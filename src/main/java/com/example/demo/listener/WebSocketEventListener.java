package com.example.demo.listener;

import com.example.demo.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent event) {
        logger.info("Received new websocket connection.");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("Received disconnection event.");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.info("Processing disconnect request from {}", username);
            ChatMessage message = new ChatMessage();
            message.setType(ChatMessage.MessageType.LEAVE);
            message.setSender(username);

            messageTemplate.convertAndSend("/topic/public", message);
            logger.info("User {} disconnected.", username);
        }
    }
}
