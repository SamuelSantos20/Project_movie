package com.br.sb.project_movie.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
public class CacheInitializer {

    private final CacheManager cacheManager;

    //Limpa todos os caches quando a aplicação é iniciada
    @EventListener(ApplicationReadyEvent.class)
    public void clearAllCachesOnStartup() {

        if (cacheManager.getCacheNames().isEmpty()) {
            System.out.println("No caches found to clear.");
        } else {
            cacheManager.getCacheNames().forEach(cacheName -> {
                cacheManager.getCache(cacheName).clear();
                System.out.println("Cleared cache: " + cacheName);
            });
        }
    }

}
