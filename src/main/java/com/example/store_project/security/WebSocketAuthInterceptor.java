package com.example.store_project.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final String ADMIN_TOPIC = "/topic/admin/";
    private static final String USER_TOPIC = "/topic/orders/";

    private final JwtService jwtService;

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        // 1) عند الاتصال: تحقق من التوكن وحدد هوية المستخدم
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new MessagingException("Missing token");
            }
            String token = header.substring(7);
            if (!jwtService.isTokenValid(token)) {
                throw new MessagingException("Invalid token");
            }
            String username = jwtService.extractUsername(token);
            String role = normalize(jwtService.extractRole(token));
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
        }

        // 2) عند الاشتراك بقناة: تحقق من الصلاحية قبل السماح
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Principal user = accessor.getUser();
            String destination = accessor.getDestination();
            if (user == null || destination == null) {
                throw new MessagingException("Not authenticated");
            }
            if (destination.startsWith(ADMIN_TOPIC)) {
                if (!isAdmin(user)) {
                    throw new MessagingException("Admin only");
                }
            } else if (destination.startsWith(USER_TOPIC)) {
                String owner = destination.substring(USER_TOPIC.length());
                if (!owner.equals(user.getName())) {
                    throw new MessagingException("Not your channel");
                }
            } else {
                throw new MessagingException("Unknown channel");
            }
        }
        return message;
    }

    private boolean isAdmin(Principal user) {
        if (user instanceof UsernamePasswordAuthenticationToken auth) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    // يقبل ADMIN أو ROLE_ADMIN
    private String normalize(String role) {
        if (role == null) return "USER";
        String r = role.trim().toUpperCase();
        return r.startsWith("ROLE_") ? r.substring(5) : r;
    }
}