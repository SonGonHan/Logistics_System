package com.logistics.shared.redis;

import com.logistics.shared.redis.config.RedisConfig;
import com.logistics.shared.redis.config.RedisProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Подключает общую конфигурацию Redis из shared-library.
 *
 * <h2>Использование:</h2>
 * <pre>
 * {@code
 * @SpringBootApplication
 * @EnableSharedRedis
 * public class UserAuthServiceApplication {
 *     // ...
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RedisConfig.class, RedisProperties.class})
public @interface EnableRedis {
}
