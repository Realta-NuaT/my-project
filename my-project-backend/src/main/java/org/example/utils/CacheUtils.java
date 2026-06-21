package org.example.utils;


import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate template;

    public <T> void saveListToCache(String key, T data, long expire){
        template.opsForValue().set();
    }
}
