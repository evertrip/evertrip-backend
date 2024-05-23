package com.evertrip.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisForSetService {

    private final RedisTemplate<String, String> redisTemplate;


    public void addToset(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void removeFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public void deleteSet(String key) {
        redisTemplate.delete(key);
    }


    public Set<String> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public boolean isMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }


}
