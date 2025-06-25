package inc.encora.inventory_manager.product.controllers;

import inc.encora.inventory_manager.common.dtos.ApiResponseDTO;
import inc.encora.inventory_manager.common.docs.ApiExamples;
import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@CrossOrigin({"http://localhost:8080"})
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management operations including CRUD operations, search, stock management, and inventory metrics")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Retrieves all products with pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Success", 
            content = @Content(examples = @ExampleObject(value = ApiExamples.PRODUCT_SUCCESS_RESPONSE)))
    @GetMapping
    private ResponseEntity<?> findAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Products retrieved successfully",
                productService.findAll(pageable),
                null
        ));
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.PRODUCT_CREATED_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Validation errors", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.VALIDATION_ERROR_RESPONSE)))
    })
    @PostMapping
    private ResponseEntity<?> save(@Valid @RequestBody ProductDTO productDTO) {
        Product product = productService.save(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Product saved successfully",
                product,
                null
        ));
    }

    @Operation(summary = "Search products", description = "Search products by name, category, and availability")
    @ApiResponse(responseCode = "200", description = "Search results", 
            content = @Content(examples = @ExampleObject(value = ApiExamples.PRODUCT_SUCCESS_RESPONSE)))
    @GetMapping("/search")
    private ResponseEntity<?> searchProducts(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            @Parameter(description = "Product name filter") @RequestParam(required = false, defaultValue = "") String name,
            @Parameter(description = "Categories to filter") @RequestParam(required = false) List<String> category,
            @Parameter(description = "Availability status") @RequestParam(required = false, defaultValue = "all") AvailabilityStatus availability
    ) {
        Page<Product> results = productService.search(pageable, name, category, availability);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Products retrieved successfully",
                results,
                null
        ));
    }

    @Operation(summary = "Update a product", description = "Updates an existing product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.PRODUCT_CREATED_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.NOT_FOUND_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Validation errors", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.VALIDATION_ERROR_RESPONSE)))
    })
    @PutMapping("/{id}")
    private ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.update(id, productDTO);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Product updated successfully",
                updatedProduct,
                null
        ));
    }

    @Operation(summary = "Delete a product", description = "Deletes a product from the inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.DELETE_SUCCESS_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.NOT_FOUND_RESPONSE)))
    })
    @DeleteMapping("/{id}")
    private ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Product deleted successfully",
                null,
                null
        ));
    }

    @Operation(summary = "Mark product as out of stock", description = "Sets product quantity to 0")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.STOCK_SUCCESS_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.NOT_FOUND_RESPONSE)))
    })
    @PostMapping("/{id}/outofstock")
    private ResponseEntity<?> markOutOfStock(@PathVariable String id) {
        productService.markProductOutOfStock(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Product marked as out of stock successfully",
                null,
                null
        ));
    }

    @Operation(summary = "Mark product as in stock", description = "Sets product quantity to default value (10)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.STOCK_SUCCESS_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Product not found", 
                    content = @Content(examples = @ExampleObject(value = ApiExamples.NOT_FOUND_RESPONSE)))
    })
    @PostMapping("/{id}/instock")
    private ResponseEntity<?> markInStock(@PathVariable String id) {
        productService.markProductInStock(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Product marked as in stock successfully",
                null,
                null
        ));
    }

    @Operation(summary = "Get all product categories", description = "Retrieves all unique product categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved", 
            content = @Content(examples = @ExampleObject(value = ApiExamples.CATEGORIES_RESPONSE)))
    @GetMapping("/categories")
    private ResponseEntity<?> findAllCategories() {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Categories retrieved successfully",
                productService.findAllCategories(),
                null
        ));
    }

    @Operation(summary = "Get inventory metrics", description = "Retrieves inventory metrics grouped by category")
    @ApiResponse(responseCode = "200", description = "Metrics retrieved", 
            content = @Content(examples = @ExampleObject(value = ApiExamples.METRICS_RESPONSE)))
    @GetMapping("/metrics")
    private ResponseEntity<?> getInventoryMetrics() {
        return ResponseEntity.ok(new ApiResponseDTO<>(
                "Ok",
                HttpStatus.OK.value(),
                "Inventory metrics retrieved successfully",
                productService.getInventoryMetrics(),
                null
        ));
    }
}
