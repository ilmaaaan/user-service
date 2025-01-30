package habittracker.userservice.config;

import habittracker.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userDetailsService;

    public SecurityConfig(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Доступ к Swagger без аутентификации
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Доступ к страницам администратора и модератора
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Доступ только для администраторов
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/habit-integration/**").permitAll()
                        .requestMatchers("/moderator/**").hasRole("MODERATOR") // Доступ только для модераторов
                        // Доступ к API для обновления профиля (доступен для всех аутентифицированных пользователей)
                        .requestMatchers("/profile/update").authenticated() // Аутентифицированные пользователи
                                                                              // могут обновлять профиль
                        // Доступ к восстановлению пароля (открыт для всех)
                        .requestMatchers("/auth/forgot-password", "/auth/reset-password").permitAll()
                        // Восстановление пароля доступно без авторизации
                        // Ограничение доступа к ресурсам пользователей
                        .requestMatchers("/profile/**").hasRole("USER") // Доступ для обычных пользователей
                        // Любой другой запрос требует аутентификации
                        .anyRequest().authenticated()
                ).logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
