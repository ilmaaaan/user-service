package habittracker.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(
                        new Info().title("UserService API")
                                .description("API для операций с пользователями и их настройками")
                                .version("1.0.0")
                );
    }
}
