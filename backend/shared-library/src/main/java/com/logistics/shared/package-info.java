/**
 * Общая библиотека для всех микросервисов логистической системы.
 *
 * Содержит переиспользуемые компоненты:
 * <ul>
 *   <li><b>validation</b> - Кастомные аннотации для валидации (@Phone, @Password)</li>
 *   <li><b>audit_action</b> - Типизация и управление типами аудиторских действий</li>
 *   <li><b>redis</b> - Общие настройки Redis</li>
 * </ul>
 *
 * Как использовать в другом микросервисе:
 * <pre>{@code
 * <dependency>
 *     <groupId>com.logistics</groupId>
 *     <artifactId>shared-library</artifactId>
 *     <version>1.0.0</version>
 * </dependency>
 *
 * // В главном классе:
 * @ComponentScan(basePackages = {"com.logistics.myservice", "com.logistics.shared"})
 * @EntityScan(basePackages = {"com.logistics.myservice", "com.logistics.shared"})
 * @EnableJpaRepositories(basePackages = {"com.logistics.myservice", "com.logistics.shared"})
 * }</pre>
 */
package com.logistics.shared;