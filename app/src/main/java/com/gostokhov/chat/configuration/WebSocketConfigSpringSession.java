package com.gostokhov.chat.configuration;

import com.gostokhov.chat.utility.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.gostokhov.chat.constant.SecurityConstant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@EnableScheduling
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfigSpringSession extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

    @Value("${chat.relay.host}")
    private String relayHost;

    @Value("${chat.relay.port}")
    private Integer relayPort;

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/queue/", "/topic/")
                .setUserDestinationBroadcast("/topic/unresolved.user.dest")
                .setUserRegistryBroadcast("/topic/registry.broadcast")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort);

        registry.setApplicationDestinationPrefixes("/chatroom");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = getAuthorizationHeader(accessor);
                    if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                        return message;
                    }

                    String token = authorizationHeader.substring(TOKEN_PREFIX.length());
                    String username = jwtTokenProvider.getSubject(token);
                    if (jwtTokenProvider.isTokenValid(username, token)) {
                        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        accessor.setUser(usernamePasswordAuthenticationToken);
                    }
                }
                return message;
            }
        });
    }

    private String getAuthorizationHeader(StompHeaderAccessor accessor) {
        Object genericHeader = accessor.getHeader(SimpMessageHeaderAccessor.NATIVE_HEADERS);
        return Optional.ofNullable(genericHeader)
                .map(Map.class::cast)
                .map(h -> h.get(AUTHORIZATION))
                .map(List.class::cast)
                .map(list -> list.stream().findFirst())
                .map(Optional::get)
                .map(Object::toString)
                .orElse(null);
    }
}
