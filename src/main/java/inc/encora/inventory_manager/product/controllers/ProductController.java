package inc.encora.inventory_manager.product.controllers;

import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin({ "http://localhost:8080" })
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    private ResponseEntity<?> findAll(@PageableDefault(
            sort = "name",
            direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @PostMapping
    private ResponseEntity<?> save(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.save(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/search")
    private ResponseEntity<?> searchProducts(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "category", required = false) List<String> category,
            @RequestParam(name = "availability", required = false, defaultValue = "all") AvailabilityStatus availability
    ) {
        Page<Product> results = productService.search(pageable, name, category, availability);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.update(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/outofstock")
    private ResponseEntity<?> markOutOfStock(@PathVariable String id) {
        productService.markProductOutOfStock(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/instock")
    private ResponseEntity<?> markInStock(@PathVariable String id) {
        productService.markProductInStock(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/categories")
    private ResponseEntity<?> findAllCategories() {
        return ResponseEntity.ok(productService.findAllCategories());
    }

    @GetMapping("/metrics")
    private ResponseEntity<?> getInventoryMetrics() {
        return ResponseEntity.ok(productService.getInventoryMetrics());
    }
}
