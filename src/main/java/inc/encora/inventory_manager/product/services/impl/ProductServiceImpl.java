package inc.encora.inventory_manager.product.services.impl;

import inc.encora.inventory_manager.common.exceptions.ResourceNotFoundException;
import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.constants.DefaultStockValues;
import inc.encora.inventory_manager.product.dtos.InventoryMetricDTO;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.mappers.ProductMapper;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.repositories.ProductRepository;
import inc.encora.inventory_manager.product.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        existingProduct.setUpdatedAt(LocalDate.now());

        return productRepository.save(existingProduct);
    }

    @Override
    public void markProductOutOfStock(String id) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setUpdatedAt(LocalDate.now());
        existingProduct.setQuantityInStock(DefaultStockValues.OUT_OF_STOCK.getValue());
        productRepository.save(existingProduct);
    }

    @Override
    public void markProductInStock(String id) {
        Product existingProduct = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existingProduct.setUpdatedAt(LocalDate.now());
        existingProduct.setQuantityInStock(DefaultStockValues.RESTORED_STOCK.getValue());
        productRepository.save(existingProduct);
    }

    @Override
    public void deleteById(String id) {
        productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> search(Pageable pageable, String name, List<String> category, AvailabilityStatus availability) {
        return productRepository.findByNameOrCategoryOrQuantityInStock(pageable, name, category, availability);
    }

    @Override
    public List<String> findAllCategories() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        List<String> categoriesList = products.stream().map(Product::getCategory).toList();
        Set<String> categoriesSet = new HashSet<>(categoriesList);
        return categoriesSet.stream().toList();
    }

    private BigDecimal calculateAveragePrice(BigDecimal totalValue, int totalQuantity) {
        if (totalQuantity == 0) {
            return BigDecimal.ZERO;
        } else {
            return totalValue.divide(
                    new BigDecimal(totalQuantity),
                    2,
                    RoundingMode.HALF_UP
            );
        }
    }

    private InventoryMetricDTO calculateMetricsForProducts(String categoryName, List<Product> products) {
        int totalProductsInStock = products.stream()
                .mapToInt(Product::getQuantityInStock)
                .sum();

        BigDecimal totalValueInStock = products.stream()
                .map(Product::getProductValueInStock)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averagePriceInStock = calculateAveragePrice(totalValueInStock, totalProductsInStock);

        return new InventoryMetricDTO(
                categoryName,
                totalProductsInStock,
                totalValueInStock,
                averagePriceInStock
        );
    }

    @Override
    public List<InventoryMetricDTO> getInventoryMetrics() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        Map<String, List<Product>> productsByCategory = products.stream().collect(Collectors.groupingBy(Product::getCategory));

        List<InventoryMetricDTO> metrics = productsByCategory.entrySet().stream()
                .map(entry -> calculateMetricsForProducts(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));


        InventoryMetricDTO overallMetric = calculateMetricsForProducts("Overall", products);
        metrics.add(overallMetric);

        return metrics;
    }
}
