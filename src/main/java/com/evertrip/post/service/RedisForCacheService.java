package com.evertrip.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

import static com.evertrip.constant.ConstantPool.viewPattern;

@Service
@RequiredArgsConstructor
public class RedisForCacheService {

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

    public HashMap<Long,Long> getViewMapForUpdate() {
         Set<String> keys = redisTemplate.keys(viewPattern);
         if (keys == null || keys.isEmpty()) {
             return new HashMap<>();
         }

        HashMap<Long, Long> keyValueMap = new HashMap<>();
        for (String key : keys) {

            Long view = Long.parseLong(redisTemplate.opsForValue().get(key));
            keyValueMap.put(Long.parseLong(key.replace("views::", "")), view);
        }
        return keyValueMap;

    }


}
