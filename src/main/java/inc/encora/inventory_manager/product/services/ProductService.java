package inc.encora.inventory_manager.product.services;

import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.dtos.InventoryMetricDTO;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Iterable<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Product save(ProductDTO newProduct);

    Product update(String id, ProductDTO productToUpdate);

    void markProductOutOfStock(String id);

    void markProductInStock(String id);

    void deleteById(String id);

    Page<Product> search(Pageable pageable, String name, List<String> category, AvailabilityStatus availability);

    List<String> findAllCategories();

    List<InventoryMetricDTO> getInventoryMetrics();
}
