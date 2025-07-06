package com.br.sb.project_movie.service;

import com.br.sb.project_movie.model.Movie;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Data
@Service
@RequiredArgsConstructor
public class MovieCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCacheManager redisCacheManager;

    public void cacheMovie(UUID uuid, Movie movie) {
        String key = "movie:" + uuid;
        redisTemplate.opsForValue().set(key, movie);
    }

    public Movie getCachedMovie(UUID uuid) {
        String key = "movie:" + uuid;
        return (Movie) redisTemplate.opsForValue().get(key);
    }

    public void removeCachedMovie(UUID uuid) {
        redisTemplate.delete("movie:" + uuid);
    }

    public void removeAllCachedMovies() {
        Set<String> keys = redisTemplate.keys("movie:*");
        if (keys != null) redisTemplate.delete(keys);
    }

    public void evictSpringCache(String cacheName) {
        redisCacheManager.getCache(cacheName).clear(); // Limpa o cache controlado por @Cacheable
    }

    public void clearCacheByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void clearAllCache() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            return null;
        });
    }

    public Movie getMovie(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Movie ID cannot be null");
        }
        String key = "movie:" + id;
        Movie movie = (Movie) redisTemplate.opsForValue().get(key);
        if (movie == null) {
            throw new IllegalArgumentException("Movie not found with id: " + id);
        }
        return movie;
    }
}
