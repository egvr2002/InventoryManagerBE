package inc.encora.inventory_manager.product.repositories.impl;

import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LocalProductRepositoryTest {

  private LocalProductRepository repository;

  private Product testProduct1;
  private Product testProduct2;
  private Product testProduct3;

  @BeforeEach
  void setUp() {
    repository = new LocalProductRepository();

    repository.deleteAll();

    testProduct1 = Product.builder()
        .name("Test Laptop")
        .category("Electronics")
        .unitPrice(new BigDecimal("999.99"))
        .expirationDate(LocalDate.now().plusYears(1))
        .quantityInStock(10)
        .build();

    testProduct2 = Product.builder()
        .name("Test Mouse")
        .category("Peripherals")
        .unitPrice(new BigDecimal("29.99"))
        .expirationDate(LocalDate.now().plusMonths(6))
        .quantityInStock(0)
        .build();

    testProduct3 = Product.builder()
        .name("Test Keyboard")
        .category("Peripherals")
        .unitPrice(new BigDecimal("79.99"))
        .expirationDate(LocalDate.now().plusYears(2))
        .quantityInStock(25)
        .build();
  }

  @Test
  void testSave_ShouldSaveAndReturnProduct() {
    Product savedProduct = repository.save(testProduct1);

    assertNotNull(savedProduct);
    assertEquals(testProduct1.getName(), savedProduct.getName());
    assertEquals(testProduct1.getCategory(), savedProduct.getCategory());
    assertEquals(testProduct1.getUnitPrice(), savedProduct.getUnitPrice());
    assertEquals(testProduct1.getQuantityInStock(), savedProduct.getQuantityInStock());
    assertNotNull(savedProduct.getId());
  }

  @Test
  void testSaveAll_ShouldSaveMultipleProducts() {
    List<Product> products = Arrays.asList(testProduct1, testProduct2, testProduct3);
    Iterable<Product> savedProducts = repository.saveAll(products);
    List<Product> savedList = new ArrayList<>();
    savedProducts.forEach(savedList::add);

    assertNotNull(savedProducts);
    assertEquals(3, savedList.size());
    assertEquals(3, repository.count());
  }

  @Test
  void testFindById_WhenProductExists_ShouldReturnProduct() {
    Product savedProduct = repository.save(testProduct1);
    String productId = savedProduct.getId();
    Optional<Product> found = repository.findById(productId);

    assertTrue(found.isPresent());
    assertEquals(savedProduct.getName(), found.get().getName());
    assertEquals(savedProduct.getCategory(), found.get().getCategory());
  }

  @Test
  void testFindById_WhenProductDoesNotExist_ShouldReturnEmpty() {
    String nonExistentId = "non-existent-id";
    Optional<Product> found = repository.findById(nonExistentId);

    assertFalse(found.isPresent());
  }

  @Test
  void testExistsById_WhenProductExists_ShouldReturnTrue() {
    Product savedProduct = repository.save(testProduct1);
    boolean exists = repository.existsById(savedProduct.getId());

    assertTrue(exists);
  }

  @Test
  void testExistsById_WhenProductDoesNotExist_ShouldReturnFalse() {
    String nonExistentId = "non-existent-id";
    boolean exists = repository.existsById(nonExistentId);

    assertFalse(exists);
  }

  @Test
  void testFindAll_ShouldReturnAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);

    Iterable<Product> allProducts = repository.findAll();
    List<Product> productList = new ArrayList<>();
    allProducts.forEach(productList::add);

    assertNotNull(allProducts);
    assertEquals(3, productList.size());
  }

  @Test
  void testFindAllById_ShouldReturnRequestedProducts() {
    Product saved1 = repository.save(testProduct1);
    Product saved2 = repository.save(testProduct2);
    repository.save(testProduct3);

    List<String> requestedIds = Arrays.asList(saved1.getId(), saved2.getId());
    Iterable<Product> foundProducts = repository.findAllById(requestedIds);
    List<Product> foundList = new ArrayList<>();
    foundProducts.forEach(foundList::add);

    assertNotNull(foundProducts);
    assertEquals(2, foundList.size());
  }

  @Test
  void testFindAllById_WithNonExistentIds_ShouldReturnOnlyExistingProducts() {
    Product saved1 = repository.save(testProduct1);
    List<String> requestedIds = Arrays.asList(saved1.getId(), "non-existent-id");

    Iterable<Product> foundProducts = repository.findAllById(requestedIds);
    List<Product> foundList = new ArrayList<>();
    foundProducts.forEach(foundList::add);

    assertNotNull(foundProducts);
    assertEquals(1, foundList.size());
    assertEquals(saved1.getId(), foundList.get(0).getId());
  }

  @Test
  void testCount_ShouldReturnCorrectCount() {
    assertEquals(0, repository.count());
    repository.save(testProduct1);
    repository.save(testProduct2);

    assertEquals(2, repository.count());
  }

  @Test
  void testDeleteById_ShouldRemoveProduct() {
    Product savedProduct = repository.save(testProduct1);
    assertEquals(1, repository.count());
    repository.deleteById(savedProduct.getId());

    assertEquals(0, repository.count());
    assertFalse(repository.existsById(savedProduct.getId()));
  }

  @Test
  void testDelete_ShouldRemoveProduct() {
    Product savedProduct = repository.save(testProduct1);
    assertEquals(1, repository.count());
    repository.delete(savedProduct);

    assertEquals(0, repository.count());
    assertFalse(repository.existsById(savedProduct.getId()));
  }

  @Test
  void testDeleteAllById_ShouldRemoveSpecifiedProducts() {
    Product saved1 = repository.save(testProduct1);
    Product saved2 = repository.save(testProduct2);
    repository.save(testProduct3);
    assertEquals(3, repository.count());
    List<String> idsToDelete = Arrays.asList(saved1.getId(), saved2.getId());
    repository.deleteAllById(idsToDelete);

    assertEquals(1, repository.count());
    assertFalse(repository.existsById(saved1.getId()));
    assertFalse(repository.existsById(saved2.getId()));
  }

  @Test
  void testDeleteAll_WithEntities_ShouldRemoveSpecifiedProducts() {
    Product saved1 = repository.save(testProduct1);
    Product saved2 = repository.save(testProduct2);
    repository.save(testProduct3);
    assertEquals(3, repository.count());
    List<Product> productsToDelete = Arrays.asList(saved1, saved2);
    repository.deleteAll(productsToDelete);

    assertEquals(1, repository.count());
    assertFalse(repository.existsById(saved1.getId()));
    assertFalse(repository.existsById(saved2.getId()));
  }

  @Test
  void testDeleteAll_ShouldRemoveAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    assertEquals(3, repository.count());
    repository.deleteAll();

    assertEquals(0, repository.count());
  }

  @Test
  void testFindAllWithPageable_ShouldReturnPagedResults() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
    Page<Product> page = repository.findAll(pageable);

    assertNotNull(page);
    assertEquals(2, page.getSize());
    assertEquals(3, page.getTotalElements());
    assertEquals(2, page.getTotalPages());
    assertEquals(0, page.getNumber());
    assertTrue(page.hasNext());
    assertFalse(page.hasPrevious());
  }

  @Test
  void testFindAllWithPageable_SecondPage_ShouldReturnCorrectResults() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));
    Page<Product> page = repository.findAll(pageable);

    assertNotNull(page);
    assertEquals(2, page.getSize());
    assertEquals(3, page.getTotalElements());
    assertEquals(2, page.getTotalPages());
    assertEquals(1, page.getNumber());
    assertFalse(page.hasNext());
    assertTrue(page.hasPrevious());
    assertEquals(1, page.getContent().size());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithNameFilter_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, "Laptop", null, null);

    assertNotNull(results);
    assertEquals(1, results.getTotalElements());
    assertEquals("Test Laptop", results.getContent().get(0).getName());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithCategoryFilter_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, Arrays.asList("Peripherals"), null);

    assertNotNull(results);
    assertEquals(2, results.getTotalElements());
    assertTrue(results.getContent().stream()
        .allMatch(p -> "Peripherals".equals(p.getCategory())));
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithAvailabilityInStock_ShouldReturnInStockProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, null, AvailabilityStatus.IN_STOCK);

    assertNotNull(results);
    assertEquals(2, results.getTotalElements());
    assertTrue(results.getContent().stream()
        .allMatch(p -> p.getQuantityInStock() > 0));
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithAvailabilityOutOfStock_ShouldReturnOutOfStockProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, null, AvailabilityStatus.OUT_OF_STOCK);

    assertNotNull(results);
    assertEquals(1, results.getTotalElements());
    assertEquals(0, results.getContent().get(0).getQuantityInStock().intValue());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithAvailabilityAll_ShouldReturnAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, null, AvailabilityStatus.ALL);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithMultipleFilters_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, Arrays.asList("Peripherals"), AvailabilityStatus.IN_STOCK);

    assertNotNull(results);
    assertEquals(1, results.getTotalElements());
    Product result = results.getContent().get(0);
    assertEquals("Peripherals", result.getCategory());
    assertTrue(result.getQuantityInStock() > 0);
    assertEquals("Test Keyboard", result.getName());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithCaseInsensitiveNameSearch_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, "LAPTOP", null, null);

    assertNotNull(results);
    assertEquals(1, results.getTotalElements());
    assertEquals("Test Laptop", results.getContent().get(0).getName());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithCaseInsensitiveCategorySearch_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, Arrays.asList("ELECTRONICS"), null);

    assertNotNull(results);
    assertEquals(1, results.getTotalElements());
    assertEquals("Electronics", results.getContent().get(0).getCategory());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithAllCategory_ShouldReturnAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, Arrays.asList("all"), null);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithEmptyCategory_ShouldReturnAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, Arrays.asList(), null);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithNullParameters_ShouldReturnAllProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, null, null);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithPartialNameMatch_ShouldReturnMatchingProducts() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, "Test", null, null);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
    assertTrue(results.getContent().stream()
        .allMatch(p -> p.getName().contains("Test")));
  }

  @Test
  void testFindByNameOrCategoryOrQuantityInStock_WithSorting_ShouldReturnSortedResults() {
    repository.save(testProduct1);
    repository.save(testProduct2);
    repository.save(testProduct3);
    Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
    Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
        pageable, null, null, null);

    assertNotNull(results);
    assertEquals(3, results.getTotalElements());
    List<Product> content = results.getContent();
    assertEquals("Test Keyboard", content.get(0).getName());
    assertEquals("Test Laptop", content.get(1).getName());
    assertEquals("Test Mouse", content.get(2).getName());
  }

  @Test
  void testSave_UpdateExistingProduct_ShouldUpdateProduct() {
    Product savedProduct = repository.save(testProduct1);
    String originalId = savedProduct.getId();
    savedProduct.setName("Updated Laptop");
    savedProduct.setUnitPrice(new BigDecimal("1199.99"));
    Product updatedProduct = repository.save(savedProduct);

    assertEquals(originalId, updatedProduct.getId());
    assertEquals("Updated Laptop", updatedProduct.getName());
    assertEquals(new BigDecimal("1199.99"), updatedProduct.getUnitPrice());
    assertEquals(1, repository.count());
  }
}
