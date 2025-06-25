package inc.encora.inventory_manager.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Manager API")
                        .description("A comprehensive inventory management system API for tracking products, categories, stock levels, and generating inventory metrics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@inventorymanager.com"))
    }
}
