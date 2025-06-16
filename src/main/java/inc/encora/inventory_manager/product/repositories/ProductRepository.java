package inc.encora.inventory_manager.product.repositories;

import inc.encora.inventory_manager.product.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


public interface ProductRepository extends CrudRepository<Product, String> {
    Page<Product> findAll(Pageable pageable);
}
