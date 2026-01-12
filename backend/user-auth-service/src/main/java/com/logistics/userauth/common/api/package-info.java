/**
 * Набор meta-аннотаций для описания REST-операций в OpenAPI/Swagger.
 *
 * Оборачивают стандартные аннотации OpenAPI (@Operation, @ApiResponse и т.д.),
 * чтобы сократить дублирование описаний в контроллерах.
 *
 * Примеры:
 * <ul>
 *   <li><b>@SignUpOperation</b> - Аннотирует endpoint регистрации</li>
 *   <li><b>@SignInOperation</b> - Аннотирует endpoint входа</li>
 *   <li><b>@RefreshOperation</b> - Аннотирует endpoint обновления токена</li>
 *   <li><b>@LogoutOperation</b> - Аннотирует endpoint выхода</li>
 *   <li><b>@SendVerificationCodeOperation</b> - Аннотирует endpoint отправления кода</li>
 *   <li><b>@LogoutOperation</b> - Аннотирует endpoint подтверждения </li>
 *   <li><b>@GetInfoOperation</b> - Аннотирует endpoint получения информации о текущем пользователе</li>
 *   <li><b>@UpdateInfoOperation</b> - Аннотирует endpoint обновления информации о текущем пользователе</li>
 * </ul>
 *
 * Использование:
 * <pre>{@code
 * @PostMapping("/sign-up")
 * @SignUpOperation
 * public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) { }
 * }</pre>
 */
package com.logistics.userauth.common.api;
