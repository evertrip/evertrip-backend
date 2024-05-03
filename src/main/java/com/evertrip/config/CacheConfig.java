package com.evertrip.config;

import com.evertrip.constant.ConstantPool;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public RedisConnectionFactory basicCacheRedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Object.class)));

        Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
        configurations.put(ConstantPool.CacheName.POST, defaultConfig.entryTtl(Duration.ofDays(1)));
        configurations.put(ConstantPool.CacheName.VIEWS, defaultConfig.entryTtl(Duration.ofDays(1)));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(basicCacheRedisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configurations)
                .build();
    }
}
