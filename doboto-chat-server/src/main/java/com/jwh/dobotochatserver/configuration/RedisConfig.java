package com.jwh.dobotochatserver.configuration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisSerializer fastJsonRedisSerializer(){
        return new FastJsonRedisSerializer(Object.class);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,RedisSerializer fastJsonRedisSerializer) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //key value 序列化器
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    // 配置缓存管理器
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,RedisSerializer fastJsonRedisSerializer) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60)) // 60s缓存失效
                // 设置key的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer))
                // 不缓存null值
                .disableCachingNullValues();

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
        return redisCacheManager;
    }

}
