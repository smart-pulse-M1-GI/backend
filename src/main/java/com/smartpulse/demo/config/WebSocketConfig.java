package com.smartpulse.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Cette annotation active la gestion des messages
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Préfixe pour les destinations des messages sortants (Serveur -> Client)
        config.enableSimpleBroker("/topic");
        // Préfixe pour les messages entrants (Client -> Serveur)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // L'endpoint pour la connexion initiale du frontend Next.js
        registry.addEndpoint("/ws-cardiac")
                .setAllowedOrigins("http://localhost:3000") // URL de votre app Next.js
                .withSockJS(); // Support pour les navigateurs anciens
    }
}