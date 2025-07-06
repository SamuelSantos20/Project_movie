package com.br.sb.project_movie.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Remove todas as chaves do Redis que começam com o prefixo fornecido.
     *
     * @param prefix Chave ou padrão (ex: "movie:" ou "status:")
     */
    public void clearCacheByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * Limpa todo o banco de dados Redis usado pela aplicação.
     */
    public void clearAllCache() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb(); // Limpa o banco de dados atual
            return null;
        });
    }
}
