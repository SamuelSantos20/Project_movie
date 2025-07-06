package com.br.sb.project_movie.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class AnalysisStatusService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATUS_PREFIX = "status:";

    public void setStatus(String id, String status) {
        String key = STATUS_PREFIX + id;
        redisTemplate.opsForValue().set(key, status);
    }

    public void setStatusWithTTL(String id, String status, Duration ttl) {
        String key = STATUS_PREFIX + id;
        redisTemplate.opsForValue().set(key, status, ttl);
    }

    public String getStatus(String id) {
        String key = STATUS_PREFIX + id;
        Object status = redisTemplate.opsForValue().get(key);
        return status != null ? status.toString() : "ID não encontrado";
    }

    public void removeStatus(String id) {
        redisTemplate.delete(STATUS_PREFIX + id);
    }
}
