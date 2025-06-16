package inc.encora.inventory_manager.product.services.impl;

import inc.encora.inventory_manager.common.exceptions.ResourceNotFoundException;
import inc.encora.inventory_manager.product.constants.DefaultStockValues;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.mappers.ProductMapper;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.repositories.ProductRepository;
import inc.encora.inventory_manager.product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product save(ProductDTO newProduct) {
        return productRepository.save(ProductMapper.toProductEntity(newProduct));
    }

    @Override
    public Product update(String id, ProductDTO productToUpdate) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setName(productToUpdate.getName());
        existingProduct.setCategory(productToUpdate.getCategory());
        existingProduct.setUnitPrice(productToUpdate.getUnitPrice());
        existingProduct.setExpirationDate(productToUpdate.getExpirationDate());
        existingProduct.setQuantityInStock(productToUpdate.getQuantityInStock());

        return productRepository.save(existingProduct);
    }

    @Override
    public void markProductOutOfStock(String id) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setQuantityInStock(DefaultStockValues.OUT_OF_STOCK.getValue());
        productRepository.save(existingProduct);
    }

    @Override
    public void markProductInStock(String id) {
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        existingProduct.setQuantityInStock(DefaultStockValues.RESTORED_STOCK.getValue());
        productRepository.save(existingProduct);
    }

    @Override
    public void deleteById(String id) {
        productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.deleteById(id);
    }
}
