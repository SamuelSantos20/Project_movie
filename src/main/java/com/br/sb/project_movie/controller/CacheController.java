package com.br.sb.project_movie.controller;

import com.br.sb.project_movie.service.MovieCacheService;
import com.br.sb.project_movie.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final RedisCacheService redisCacheService;
    private final MovieCacheService movieCacheService;

    // 🔁 Limpa todo o Redis (flushDb)
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAllRedis() {
        redisCacheService.clearAllCache();
        return ResponseEntity.ok("Todos os dados do Redis foram removidos.");
    }

    // 🔁 Limpa por prefixo direto no Redis (ex: movie:, status:, etc.)
    @DeleteMapping("/clear/prefix/{prefix}")
    public ResponseEntity<String> clearByPrefix(@PathVariable String prefix) {
        redisCacheService.clearCacheByPrefix(prefix);
        return ResponseEntity.ok("Cache com prefixo '" + prefix + "' removido com sucesso.");
    }

    // 🔁 Limpa todos os filmes em cache (chave: movie:*)
    @DeleteMapping("/movie/clear-all")
    public ResponseEntity<String> clearAllMovieCache() {
        movieCacheService.clearAllCache();
        return ResponseEntity.ok("Todos os filmes em cache foram removidos.");
    }

    // 🔁 Remove cache de um filme específico por UUID
    @DeleteMapping("/movie/{uuid}")
    public ResponseEntity<String> clearMovieCacheById(@PathVariable UUID uuid) {
        movieCacheService.removeCachedMovie(uuid);
        return ResponseEntity.ok("Cache do filme " + uuid + " removido com sucesso.");
    }

    // 🔁 (Opcional) Limpa cache de nome registrado no @Cacheable
    @DeleteMapping("/spring-cache/{name}")
    public ResponseEntity<String> evictSpringCache(@PathVariable String name) {
        movieCacheService.evictSpringCache(name);
        return ResponseEntity.ok("Cache Spring '" + name + "' limpo.");
    }
}
