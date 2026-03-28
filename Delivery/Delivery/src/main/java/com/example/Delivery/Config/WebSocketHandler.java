package com.example.Delivery.Config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // Map: deliveryBoyId -> session
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        URI uri = session.getUri();

        if (uri == null || uri.getQuery() == null) {
            session.close();
            return;
        }

        String deliveryBoyId = null;

        for (String param : uri.getQuery().split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2 && parts[0].equals("phone")) {
                deliveryBoyId = parts[1];
                break;
            }
        }

        if (deliveryBoyId == null || deliveryBoyId.isBlank()) {
            session.close();
            return;
        }

        sessions.put(deliveryBoyId, session);

        System.out.println("WebSocket connected for deliveryBoyId: " + deliveryBoyId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received from client: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));

        System.out.println("WebSocket disconnected: " + status);
    }


    public static void sendToDeliveryBoy(String deliveryBoyId, String json) {

        WebSocketSession session = sessions.get(deliveryBoyId);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No active socket for deliveryBoyId: " + deliveryBoyId);
        }
    }
}
