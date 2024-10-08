package com.weatherclaus.be.cofig;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager boardCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // ObjectMapper 설정
        ObjectMapper objectMapper = new ObjectMapper();

        // JavaTimeModule 및 LocalDateTimeSerializer 설정
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        objectMapper.registerModule(javaTimeModule);

        // Hibernate 모듈 추가 (프록시 객체 처리)
        objectMapper.registerModule(new Hibernate6Module());

        // Page 인터페이스 처리
        SimpleModule pageModule = new SimpleModule();
        pageModule.addAbstractTypeMapping(Page.class, PageImpl.class);
        objectMapper.registerModule(pageModule);

        // 타입 정보 활성화 (업데이트된 방식으로 설정)
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );


        // Jackson2JsonRedisSerializer에 ObjectMapper 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.setObjectMapper(objectMapper);

        // Redis Cache 설정 (Key, Value 직렬화 방식 및 TTL 설정)
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))  // Jackson2JsonRedisSerializer 사용
                .entryTtl(Duration.ofMinutes(60L)); // TTL 60분 설정

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}