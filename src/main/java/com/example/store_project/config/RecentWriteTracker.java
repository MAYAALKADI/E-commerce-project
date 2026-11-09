package com.example.store_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RecentWriteTracker {

    private final ConcurrentHashMap<String, Long> lastWrite = new ConcurrentHashMap<>();
    private final long windowMillis;

    public RecentWriteTracker(@Value("${replica.read-your-writes-seconds:10}") long seconds) {
        this.windowMillis = seconds * 1000;
    }

    public void markWritten(String username) {
        if (username != null) {
            lastWrite.put(username, System.currentTimeMillis());
        }
    }

    public boolean hasRecentWrite(String username) {
        Long t = lastWrite.get(username);
        if (t == null) return false;
        if (System.currentTimeMillis() - t > windowMillis) {
            lastWrite.remove(username, t);
            return false;
        }
        return true;
    }
}