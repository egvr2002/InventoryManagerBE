package inc.encora.inventory_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class InventoryManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagerApplication.class, args);
	}

}
