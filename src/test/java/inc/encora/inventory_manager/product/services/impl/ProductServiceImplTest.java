package inc.encora.inventory_manager.product.services.impl;

import inc.encora.inventory_manager.common.exceptions.ResourceNotFoundException;
import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.constants.DefaultStockValues;
import inc.encora.inventory_manager.product.dtos.InventoryMetricDTO;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductServiceImpl productService;

  private Product testProduct;
  private ProductDTO testProductDTO;
  private String testProductId;

  @BeforeEach
  void setUp() {
    testProductId = "test-id-123";

    testProduct = Product.builder()
        .name("Test Laptop")
        .category("Electronics")
        .unitPrice(new BigDecimal("999.99"))
        .expirationDate(LocalDate.now().plusYears(1))
        .quantityInStock(10)
        .build();

    testProductDTO = new ProductDTO(
        "Test Laptop",
        "Electronics",
        new BigDecimal("999.99"),
        LocalDate.now().plusYears(1),
        10);
  }

  @Test
  void testFindAll_ShouldReturnAllProducts() {
    List<Product> expectedProducts = Arrays.asList(testProduct);
    when(productRepository.findAll()).thenReturn(expectedProducts);

    Iterable<Product> result = productService.findAll();

    assertNotNull(result);
    assertEquals(expectedProducts, result);
    verify(productRepository).findAll();
  }

  @Test
  void testFindAll_WithPageable_ShouldReturnPagedProducts() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
    when(productRepository.findAll(pageable)).thenReturn(expectedPage);

    Page<Product> result = productService.findAll(pageable);

    assertNotNull(result);
    assertEquals(expectedPage, result);
    verify(productRepository).findAll(pageable);
  }

  @Test
  void testSave_ShouldSaveAndReturnProduct() {
    when(productRepository.save(any(Product.class))).thenReturn(testProduct);

    Product result = productService.save(testProductDTO);

    assertNotNull(result);
    assertEquals(testProduct, result);
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void testUpdate_WhenProductExists_ShouldUpdateAndReturnProduct() {
    Product existingProduct = Product.builder()
        .name("Old Name")
        .category("Old Category")
        .unitPrice(new BigDecimal("500.00"))
        .expirationDate(LocalDate.now().plusMonths(6))
        .quantityInStock(5)
        .build();

    when(productRepository.findById(testProductId)).thenReturn(Optional.of(existingProduct));
    when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

    Product result = productService.update(testProductId, testProductDTO);

    assertNotNull(result);
    assertEquals(testProductDTO.getName(), existingProduct.getName());
    assertEquals(testProductDTO.getCategory(), existingProduct.getCategory());
    assertEquals(testProductDTO.getUnitPrice(), existingProduct.getUnitPrice());
    assertEquals(testProductDTO.getExpirationDate(), existingProduct.getExpirationDate());
    assertEquals(testProductDTO.getQuantityInStock(), existingProduct.getQuantityInStock());
    assertEquals(LocalDate.now(), existingProduct.getUpdatedAt());
    verify(productRepository).findById(testProductId);
    verify(productRepository).save(existingProduct);
  }

  @Test
  void testUpdate_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> productService.update(testProductId, testProductDTO));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository).findById(testProductId);
    verify(productRepository, never()).save(any());
  }

  @Test
  void testMarkProductOutOfStock_WhenProductExists_ShouldSetQuantityToZero() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
    when(productRepository.save(testProduct)).thenReturn(testProduct);

    productService.markProductOutOfStock(testProductId);

    assertEquals(DefaultStockValues.OUT_OF_STOCK.getValue(), testProduct.getQuantityInStock());
    assertEquals(LocalDate.now(), testProduct.getUpdatedAt());
    verify(productRepository).findById(testProductId);
    verify(productRepository).save(testProduct);
  }

  @Test
  void testMarkProductOutOfStock_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> productService.markProductOutOfStock(testProductId));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository).findById(testProductId);
    verify(productRepository, never()).save(any());
  }

  @Test
  void testMarkProductInStock_WhenProductExists_ShouldSetQuantityToDefault() {
    testProduct.setQuantityInStock(0);
    when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
    when(productRepository.save(testProduct)).thenReturn(testProduct);

    productService.markProductInStock(testProductId);

    assertEquals(DefaultStockValues.RESTORED_STOCK.getValue(), testProduct.getQuantityInStock());
    assertEquals(LocalDate.now(), testProduct.getUpdatedAt());
    verify(productRepository).findById(testProductId);
    verify(productRepository).save(testProduct);
  }

  @Test
  void testMarkProductInStock_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> productService.markProductInStock(testProductId));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository).findById(testProductId);
    verify(productRepository, never()).save(any());
  }

  @Test
  void testDeleteById_WhenProductExists_ShouldDeleteProduct() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));

    productService.deleteById(testProductId);

    verify(productRepository).findById(testProductId);
    verify(productRepository).deleteById(testProductId);
  }

  @Test
  void testDeleteById_WhenProductNotFound_ShouldThrowResourceNotFoundException() {
    when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> productService.deleteById(testProductId));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository).findById(testProductId);
    verify(productRepository, never()).deleteById(any());
  }

  @Test
  void testSearch_ShouldDelegateToRepository() {
    Pageable pageable = PageRequest.of(0, 10);
    String name = "laptop";
    List<String> categories = Arrays.asList("Electronics");
    AvailabilityStatus availability = AvailabilityStatus.IN_STOCK;
    Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));

    when(productRepository.findByNameOrCategoryOrQuantityInStock(pageable, name, categories, availability))
        .thenReturn(expectedPage);

    Page<Product> result = productService.search(pageable, name, categories, availability);

    assertEquals(expectedPage, result);
    verify(productRepository).findByNameOrCategoryOrQuantityInStock(pageable, name, categories, availability);
  }

  @Test
  void testFindAllCategories_ShouldReturnUniqueCategories() {
    Product product1 = Product.builder().category("Electronics").build();
    Product product2 = Product.builder().category("Peripherals").build();
    Product product3 = Product.builder().category("Electronics").build();
    List<Product> products = Arrays.asList(product1, product2, product3);

    when(productRepository.findAll()).thenReturn(products);

    List<String> result = productService.findAllCategories();

    assertEquals(2, result.size());
    assertTrue(result.contains("Electronics"));
    assertTrue(result.contains("Peripherals"));
    verify(productRepository).findAll();
  }

  @Test
  void testFindAllCategories_WhenEmptyRepository_ShouldReturnEmptyList() {
    when(productRepository.findAll()).thenReturn(Arrays.asList());

    List<String> result = productService.findAllCategories();

    assertTrue(result.isEmpty());
    verify(productRepository).findAll();
  }

  @Test
  void testGetInventoryMetrics_ShouldCalculateCorrectMetrics() {
    Product electronics1 = Product.builder()
        .category("Electronics")
        .unitPrice(new BigDecimal("100.00"))
        .quantityInStock(10)
        .build();

    Product electronics2 = Product.builder()
        .category("Electronics")
        .unitPrice(new BigDecimal("200.00"))
        .quantityInStock(5)
        .build();

    Product peripherals = Product.builder()
        .category("Peripherals")
        .unitPrice(new BigDecimal("50.00"))
        .quantityInStock(20)
        .build();

    List<Product> products = Arrays.asList(electronics1, electronics2, peripherals);
    when(productRepository.findAll()).thenReturn(products);

    List<InventoryMetricDTO> result = productService.getInventoryMetrics();

    assertEquals(3, result.size());

    InventoryMetricDTO electronicsMetric = result.stream()
        .filter(m -> "Electronics".equals(m.getCategory()))
        .findFirst()
        .orElseThrow();
    assertEquals(15, electronicsMetric.getTotalProductsInStock());
    assertEquals(new BigDecimal("2000.00"), electronicsMetric.getTotalValueInStock());
    assertEquals(new BigDecimal("133.33"), electronicsMetric.getAveragePriceInStock());

    InventoryMetricDTO peripheralsMetric = result.stream()
        .filter(m -> "Peripherals".equals(m.getCategory()))
        .findFirst()
        .orElseThrow();
    assertEquals(20, peripheralsMetric.getTotalProductsInStock());
    assertEquals(new BigDecimal("1000.00"), peripheralsMetric.getTotalValueInStock());
    assertEquals(new BigDecimal("50.00"), peripheralsMetric.getAveragePriceInStock());

    InventoryMetricDTO overallMetric = result.stream()
        .filter(m -> "Overall".equals(m.getCategory()))
        .findFirst()
        .orElseThrow();
    assertEquals(35, overallMetric.getTotalProductsInStock());
    assertEquals(new BigDecimal("3000.00"), overallMetric.getTotalValueInStock());
    assertEquals(new BigDecimal("85.71"), overallMetric.getAveragePriceInStock());

    verify(productRepository).findAll();
  }

  @Test
  void testGetInventoryMetrics_WhenEmptyRepository_ShouldReturnOverallMetricOnly() {
    when(productRepository.findAll()).thenReturn(Arrays.asList());

    List<InventoryMetricDTO> result = productService.getInventoryMetrics();

    assertEquals(1, result.size());

    InventoryMetricDTO overallMetric = result.get(0);
    assertEquals("Overall", overallMetric.getCategory());
    assertEquals(0, overallMetric.getTotalProductsInStock());
    assertEquals(BigDecimal.ZERO, overallMetric.getTotalValueInStock());
    assertEquals(BigDecimal.ZERO, overallMetric.getAveragePriceInStock());

    verify(productRepository).findAll();
  }

  @Test
  void testGetInventoryMetrics_WhenZeroQuantityProducts_ShouldCalculateCorrectly() {
    Product inStock = Product.builder()
        .category("Electronics")
        .unitPrice(new BigDecimal("100.00"))
        .quantityInStock(10)
        .build();

    Product outOfStock = Product.builder()
        .category("Electronics")
        .unitPrice(new BigDecimal("200.00"))
        .quantityInStock(0)
        .build();

    List<Product> products = Arrays.asList(inStock, outOfStock);
    when(productRepository.findAll()).thenReturn(products);

    List<InventoryMetricDTO> result = productService.getInventoryMetrics();

    assertEquals(2, result.size());

    InventoryMetricDTO electronicsMetric = result.stream()
        .filter(m -> "Electronics".equals(m.getCategory()))
        .findFirst()
        .orElseThrow();
    assertEquals(10, electronicsMetric.getTotalProductsInStock());
    assertEquals(new BigDecimal("1000.00"), electronicsMetric.getTotalValueInStock());
    assertEquals(new BigDecimal("100.00"), electronicsMetric.getAveragePriceInStock());

    verify(productRepository).findAll();
  }
}
