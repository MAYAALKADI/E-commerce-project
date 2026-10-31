package com.example.store_project.user.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPTS = 5; 
    private final int BLOCK_DURATION_MINUTES = 1;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        blockCache.remove(key);
    }

    public void loginFailed(String key) {
        if (isBlocked(key)) {
            return;
        }

        int attempts = attemptsCache.getOrDefault(key, 0) + 1;
        attemptsCache.put(key, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            blockCache.put(key, LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES));
            attemptsCache.remove(key);
        }
    }

    public boolean isBlocked(String key) {
        LocalDateTime blockExpiry = blockCache.get(key);
        if (blockExpiry == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(blockExpiry)) {
            blockCache.remove(key);
            attemptsCache.remove(key);
            return false;
        }

        return true;
    }
}