package com.logistics.shared.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Общая конфигурация Redis для всех микросервисов.
 *
 * <h2>Использование</h2>
 * Добавьте аннотацию @EnableSharedRedis в главный класс приложения:
 * <pre>
 * {@code
 * @SpringBootApplication
 * @EnableSharedRedis
 * public class UserAuthServiceApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(UserAuthServiceApplication.class, args);
 *     }
 * }
 * }
 * </pre>
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "logistics.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

    /**
     * RedisTemplate для работы с объектами (JSON сериализация).
     * Используется для кэширования сложных объектов, хранения SMS кодов и т.д.
     */

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory,
                                                       ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // ВАЖНО: отдельный ObjectMapper для Redis, чтобы не ломать HTTP JSON
        ObjectMapper redisMapper = objectMapper.copy();

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.logistics.")   // ваши доменные классы
                .allowIfSubType("java.time.")       // LocalDateTime и т.п.
                .allowIfSubType("java.util.")       // Map/List, если где-то нужно
                .build();

        redisMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(redisMapper);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate для работы с простыми строковыми значениями.
     * Используется для rate limiting, счетчиков и простых флагов.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
