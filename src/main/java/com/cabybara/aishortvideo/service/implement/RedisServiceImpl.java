package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.service.interfaces.RedisService;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value, long timeoutInSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean expire(String key, long timeoutInSeconds) {
        return redisTemplate.expire(key, timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
