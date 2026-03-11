package com.charter.retail.rewards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for API documentation.
 */
@Configuration
public class SwaggerConfig {

    /**Configures the OpenAPI documentation bean.*/
    @Bean
    public OpenAPI rewardsAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rewards API")
                        .description("API for managing customer rewards points")
                        .version("1.0"));
    }
}
