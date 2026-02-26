package com.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration
 * 
 * Enables WebSocket communication with STOMP protocol for real-time features.
 * STOMP provides a pub/sub messaging pattern over WebSocket.
 * 
 * How it works:
 * 1. Client connects to /ws endpoint
 * 2. Server creates a STOMP session
 * 3. Client subscribes to /topic/low-stock
 * 4. Server publishes messages to /topic/low-stock
 * 5. All subscribed clients receive the message
 * 
 * Interview Points:
 * - WebSocket vs HTTP: persistent connection, real-time bidirectional communication
 * - STOMP protocol: adds message headers, subscription management
 * - Message brokers: enables pub/sub pattern
 * - Why use SimpMessagingTemplate in services
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    /**
     * Configure message broker for pub/sub
     * 
     * - setApplicationDestinationPrefixes: prefix for messages FROM client
     * - enableSimpleBroker: enables in-memory broker for pub/sub TO client
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for messages from client to server
        config.setApplicationDestinationPrefixes("/app");
        
        // Enable simple in-memory broker for messages to clients
        // /topic is for pub/sub (broadcast to all subscribers)
        // /queue is for point-to-point (single recipient)
        config.enableSimpleBroker("/topic", "/queue");
    }
    
    /**
     * Register WebSocket endpoints
     * 
     * Clients connect to this endpoint to establish WebSocket connection
     * .withSockJS() provides fallback for browsers without WebSocket support
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Main WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")  // Allow Angular dev server
                .withSockJS();  // SockJS fallback
        
        // Optional: separate endpoint without SockJS for modern browsers
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200");
    }
}
