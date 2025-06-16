package inc.encora.inventory_manager.product.services;

import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Iterable<Product> findAll();

    Page<Product> findAll(Pageable pageable);

    Product save(ProductDTO newProduct);

    Product update(String id, ProductDTO productToUpdate);

    void markProductOutOfStock(String id);

    void markProductInStock(String id);

    void deleteById(String id);
}
