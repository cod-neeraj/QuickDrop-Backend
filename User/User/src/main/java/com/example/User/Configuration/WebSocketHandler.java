package com.example.User.Configuration;

import com.example.User.DTO.SellerDashBoardOrderList;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        String sellerId = null;

        for (String param : uri.getQuery().split("&")) {
            String[] parts = param.split("=");
            if (parts.length == 2 && parts[0].equals("sellerId")) {
                sellerId = parts[1];
                break;
            }
        }

        if (sellerId == null || sellerId.isBlank()) {
            session.close();
            return;
        }

        sessions.put(sellerId, session);

        System.out.println("WebSocket connected for sellerId: " + sellerId);
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


    public static void sendToseller(String sellerId, SellerDashBoardOrderList sellerDashBoardOrderList) {

        WebSocketSession session = sessions.get(sellerId);

        if (session != null && session.isOpen()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(sellerDashBoardOrderList);
                System.out.println("Sending JSON: " + json);
                session.sendMessage(new TextMessage(json));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No active socket for sellerId: " + sellerId);
        }
    }
}
