package com.sujay.urlshortener.service;

import com.sujay.urlshortener.cache.model.CachedUrl;
import com.sujay.urlshortener.cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

//@Component
//@RequiredArgsConstructor
//public class RedisConnectionTest implements CommandLineRunner {
//
//    private final RedisTemplate<String,Object> redisTemplate;
//
//    @Override
//    public void run(String... args) {
//
//        redisTemplate.opsForValue().set(
//                "test",
//                "Redis Connected Successfully"
//        );
//
//        Object value =
//                redisTemplate.opsForValue().get("test");
//
//        System.out.println(value);
//
//    }
//
//}


@Component
@RequiredArgsConstructor
public class RedisConnectionTest implements CommandLineRunner {

    private final RedisCacheService redisCacheService;

    @Override
    public void run(String... args) {

        CachedUrl cachedUrl = CachedUrl.builder()
                .originalUrl("https://github.com")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        redisCacheService.save(
                "abc123",
                cachedUrl,
                    Duration.ofMinutes(5)
        );

        System.out.println(redisCacheService.get("abc123"));

        redisCacheService.delete("abc123");

        System.out.println(redisCacheService.get("abc123"));
    }
}
