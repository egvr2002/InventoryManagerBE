package inc.encora.inventory_manager.product.repositories.impl;

import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {
        @Test
        @DisplayName("When repository is initially empty then count is zero and findAll is empty")
        void shouldReturnZeroCountAndEmptyList_whenRepositoryIsEmpty() {
            assertEquals(0, repository.count());
            assertFalse(repository.findAll().iterator().hasNext());
        }

        @Test
        @DisplayName("When adding a product then repository contains one product with correct ID")
        void shouldContainOneProductWithId_whenAddingProduct() {
            Product saved = repository.save(testProduct1);
            assertAll(
                    () -> assertNotNull(saved.getId()),
                    () -> assertEquals(1, repository.count()),
                    () -> assertTrue(repository.existsById(saved.getId()))
            );
        }

        @Test
        @DisplayName("When retrieving a product by ID after adding then correct product is returned")
        void shouldReturnCorrectProduct_whenRetrievingByIdAfterAdding() {
            Product saved = repository.save(testProduct1);
            Optional<Product> found = repository.findById(saved.getId());
            assertAll(
                    () -> assertTrue(found.isPresent()),
                    () -> assertEquals(saved.getName(), found.get().getName())
            );
        }

        @Test
        @DisplayName("When updating a product then changes are persisted")
        void shouldPersistChanges_whenUpdatingProduct() {
            Product saved = repository.save(testProduct1);
            saved.setName("Updated Name");
            saved.setQuantityInStock(99);
            Product updated = repository.save(saved);
            assertAll(
                    () -> assertEquals("Updated Name", updated.getName()),
                    () -> assertEquals(99, updated.getQuantityInStock())
            );
        }

        @Test
        @DisplayName("When removing a product then repository is empty and product does not exist")
        void shouldBeEmptyAndProductNotExist_whenRemovingProduct() {
            Product saved = repository.save(testProduct1);
            repository.delete(saved);
            assertAll(
                    () -> assertEquals(0, repository.count()),
                    () -> assertFalse(repository.existsById(saved.getId()))
            );
        }

        @Test
        @DisplayName("When removing a product by ID then product is deleted and repository is empty")
        void shouldDeleteProductAndBeEmpty_whenRemovingById() {
            Product savedProduct = repository.save(testProduct1);
            assertEquals(1, repository.count());
            repository.deleteById(savedProduct.getId());
            assertAll(
                    () -> assertEquals(0, repository.count()),
                    () -> assertFalse(repository.existsById(savedProduct.getId()))
            );
        }

        @Test
        @DisplayName("When counting products then correct count is returned")
        void shouldReturnCorrectCount_whenCountingProducts() {
            assertEquals(0, repository.count());
            repository.save(testProduct1);
            repository.save(testProduct2);
            assertEquals(2, repository.count());
        }

        @Test
        @DisplayName("When updating product with non-existent ID then product is added as new")
        void shouldAddProductAsNew_whenUpdatingWithNonExistentId() {
            Product nonExistent = Product.builder()
                    .id("non-existent-id")
                    .name("Ghost")
                    .category("Test")
                    .unitPrice(new BigDecimal("1.00"))
                    .expirationDate(LocalDate.now())
                    .quantityInStock(1)
                    .build();
            Product updated = repository.save(nonExistent);
            assertEquals("non-existent-id", updated.getId());
            assertEquals(1, repository.count());
        }

        @Test
        @DisplayName("When saving product with boundary values then product is saved correctly")
        void shouldSaveCorrectly_whenSavingProductWithBoundaryValues() {
            Product boundaryProduct = Product.builder()
                    .name("Boundary")
                    .category("Test")
                    .unitPrice(new BigDecimal(Integer.MAX_VALUE))
                    .expirationDate(LocalDate.now())
                    .quantityInStock(Integer.MAX_VALUE)
                    .build();
            Product saved = repository.save(boundaryProduct);
            assertAll(
                    () -> assertEquals(Integer.MAX_VALUE, saved.getQuantityInStock()),
                    () -> assertEquals(new BigDecimal(Integer.MAX_VALUE), saved.getUnitPrice())
            );
        }

        @Test
        @DisplayName("When saving expired product then expiration date is before current date")
        void shouldHaveExpirationBeforeCurrentDate_whenSavingExpiredProduct() {
            Product expiredProduct = Product.builder()
                    .name("Expired")
                    .category("Test")
                    .unitPrice(new BigDecimal("10.00"))
                    .expirationDate(LocalDate.of(2020, 1, 1))
                    .quantityInStock(1)
                    .build();
            Product saved = repository.save(expiredProduct);
            assertAll(
                    () -> assertEquals(LocalDate.of(2020, 1, 1), saved.getExpirationDate()),
                    () -> assertTrue(saved.getExpirationDate().isBefore(LocalDate.of(2025, 10, 17)))
            );
        }
    }

    @Nested
    @DisplayName("Bulk Operations")
    class BulkOperations {
        @Test
        @DisplayName("When adding multiple products then repository contains all products")
        void shouldContainAllProducts_whenAddingMultipleProducts() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            assertEquals(3, repository.count());
        }

        @Test
        @DisplayName("When retrieving all products after adding multiple then all products are returned")
        void shouldReturnAllProducts_whenRetrievingAfterAddingMultiple() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Iterable<Product> allProducts = repository.findAll();
            List<Product> productList = new ArrayList<>();
            allProducts.forEach(productList::add);
            assertAll(
                    () -> assertNotNull(allProducts),
                    () -> assertEquals(3, productList.size())
            );
        }

        @Test
        @DisplayName("When retrieving products by IDs then only requested products are returned")
        void shouldReturnOnlyRequestedProducts_whenRetrievingByIds() {
            Product saved1 = repository.save(testProduct1);
            Product saved2 = repository.save(testProduct2);
            repository.save(testProduct3);
            List<String> requestedIds = Arrays.asList(saved1.getId(), saved2.getId());
            Iterable<Product> foundProducts = repository.findAllById(requestedIds);
            List<Product> foundList = new ArrayList<>();
            foundProducts.forEach(foundList::add);
            assertAll(
                    () -> assertNotNull(foundProducts),
                    () -> assertEquals(2, foundList.size())
            );
        }

        @Test
        @DisplayName("When some IDs do not exist then only existing products are returned")
        void shouldReturnOnlyExistingProducts_whenSomeIdsDoNotExist() {
            Product saved1 = repository.save(testProduct1);
            List<String> requestedIds = Arrays.asList(saved1.getId(), "non-existent-id");
            Iterable<Product> foundProducts = repository.findAllById(requestedIds);
            List<Product> foundList = new ArrayList<>();
            foundProducts.forEach(foundList::add);
            assertAll(
                    () -> assertNotNull(foundProducts),
                    () -> assertEquals(1, foundList.size()),
                    () -> assertEquals(saved1.getId(), foundList.get(0).getId())
            );
        }

        @Test
        @DisplayName("When removing a product by entity then product is deleted and repository is empty")
        void shouldDeleteProductAndBeEmpty_whenRemovingByEntity() {
            Product savedProduct = repository.save(testProduct1);
            assertEquals(1, repository.count());
            repository.delete(savedProduct);
            assertAll(
                    () -> assertEquals(0, repository.count()),
                    () -> assertFalse(repository.existsById(savedProduct.getId()))
            );
        }

        @Test
        @DisplayName("When removing specified products by IDs then only remaining products exist")
        void shouldHaveOnlyRemainingProducts_whenRemovingSpecifiedByIds() {
            Product saved1 = repository.save(testProduct1);
            Product saved2 = repository.save(testProduct2);
            repository.save(testProduct3);
            assertEquals(3, repository.count());
            List<String> idsToDelete = Arrays.asList(saved1.getId(), saved2.getId());
            repository.deleteAllById(idsToDelete);
            assertAll(
                    () -> assertEquals(1, repository.count()),
                    () -> assertFalse(repository.existsById(saved1.getId())),
                    () -> assertFalse(repository.existsById(saved2.getId()))
            );
        }

        @Test
        @DisplayName("When removing specified products by entity then only remaining products exist")
        void shouldHaveOnlyRemainingProducts_whenRemovingSpecifiedByEntity() {
            Product saved1 = repository.save(testProduct1);
            Product saved2 = repository.save(testProduct2);
            repository.save(testProduct3);
            assertEquals(3, repository.count());
            List<Product> productsToDelete = Arrays.asList(saved1, saved2);
            repository.deleteAll(productsToDelete);
            assertAll(
                    () -> assertEquals(1, repository.count()),
                    () -> assertFalse(repository.existsById(saved1.getId())),
                    () -> assertFalse(repository.existsById(saved2.getId()))
            );
        }

        @Test
        @DisplayName("When removing all products then repository is empty")
        void shouldBeEmpty_whenRemovingAllProducts() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            assertEquals(3, repository.count());
            repository.deleteAll();
            assertEquals(0, repository.count());
        }
    }

    @Nested
    @DisplayName("Pagination")
    class Pagination {
        @Test
        @DisplayName("When requesting paged results then correct page and size are returned")
        void shouldReturnCorrectPageAndSize_whenRequestingPagedResults() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
            Page<Product> page = repository.findAll(pageable);
            assertAll(
                    () -> assertNotNull(page),
                    () -> assertEquals(2, page.getSize()),
                    () -> assertEquals(3, page.getTotalElements()),
                    () -> assertEquals(2, page.getTotalPages()),
                    () -> assertEquals(0, page.getNumber()),
                    () -> assertTrue(page.hasNext()),
                    () -> assertFalse(page.hasPrevious())
            );
        }

        @Test
        @DisplayName("When requesting second page then correct results are returned")
        void shouldReturnCorrectResults_whenRequestingSecondPage() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));
            Page<Product> page = repository.findAll(pageable);
            assertAll(
                    () -> assertNotNull(page),
                    () -> assertEquals(2, page.getSize()),
                    () -> assertEquals(3, page.getTotalElements()),
                    () -> assertEquals(2, page.getTotalPages()),
                    () -> assertEquals(1, page.getNumber()),
                    () -> assertFalse(page.hasNext()),
                    () -> assertTrue(page.hasPrevious()),
                    () -> assertEquals(1, page.getContent().size())
            );
        }

        @Test
        @DisplayName("When requesting out-of-bounds page then empty page is returned")
        void shouldReturnEmptyPage_whenRequestingOutOfBoundsPage() {
            repository.save(testProduct1);
            Pageable pageable = PageRequest.of(10, 5);
            Page<Product> page = repository.findAll(pageable);
            assertAll(
                    () -> assertNotNull(page),
                    () -> assertEquals(0, page.getContent().size())
            );
        }
    }

    @Nested
    @DisplayName("Filtering and Search")
    class FilteringAndSearch {
        @Test
        @DisplayName("When filtering by name then only matching products are returned")
        void shouldReturnMatchingProducts_whenFilteringByName() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, "Laptop", null, null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(1, results.getTotalElements()),
                    () -> assertEquals("Test Laptop", results.getContent().get(0).getName())
            );
        }

        @Test
        @DisplayName("When filtering by category then only matching products are returned")
        void shouldReturnMatchingProducts_whenFilteringByCategory() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, Arrays.asList("Peripherals"), null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(2, results.getTotalElements()),
                    () -> assertTrue(results.getContent().stream()
                            .allMatch(p -> "Peripherals".equals(p.getCategory())))
            );
        }

        @Test
        @DisplayName("When filtering by IN_STOCK availability then only in-stock products are returned")
        void shouldReturnInStockProducts_whenFilteringByInStockAvailability() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, null, AvailabilityStatus.IN_STOCK);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(2, results.getTotalElements()),
                    () -> assertTrue(results.getContent().stream()
                            .allMatch(p -> p.getQuantityInStock() > 0))
            );
        }

        @Test
        @DisplayName("When filtering by OUT_OF_STOCK availability then only out-of-stock products are returned")
        void shouldReturnOutOfStockProducts_whenFilteringByOutOfStockAvailability() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, null, AvailabilityStatus.OUT_OF_STOCK);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(1, results.getTotalElements()),
                    () -> assertEquals(0, results.getContent().get(0).getQuantityInStock().intValue())
            );
        }

        @Test
        @DisplayName("When filtering by ALL availability then all products are returned")
        void shouldReturnAllProducts_whenFilteringByAllAvailability() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, null, AvailabilityStatus.ALL);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements())
            );
        }

        @Test
        @DisplayName("When multiple filters are applied then only matching products are returned")
        void shouldReturnMatchingProducts_whenApplyingMultipleFilters() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, Arrays.asList("Peripherals"), AvailabilityStatus.IN_STOCK);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(1, results.getTotalElements()),
                    () -> {
                        Product result = results.getContent().get(0);
                        assertEquals("Peripherals", result.getCategory());
                        assertTrue(result.getQuantityInStock() > 0);
                        assertEquals("Test Keyboard", result.getName());
                    }
            );
        }

        @Test
        @DisplayName("When name search is case-insensitive then matching products are returned")
        void shouldReturnMatchingProducts_whenNameSearchIsCaseInsensitive() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, "LAPTOP", null, null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(1, results.getTotalElements()),
                    () -> assertEquals("Test Laptop", results.getContent().get(0).getName())
            );
        }

        @Test
        @DisplayName("When category search is case-insensitive then matching products are returned")
        void shouldReturnMatchingProducts_whenCategorySearchIsCaseInsensitive() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, Arrays.asList("ELECTRONICS"), null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(1, results.getTotalElements()),
                    () -> assertEquals("Electronics", results.getContent().get(0).getCategory())
            );
        }

        @Test
        @DisplayName("When 'all' category is requested then all products are returned")
        void shouldReturnAllProducts_whenAllCategoryIsRequested() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, Arrays.asList("all"), null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements())
            );
        }

        @Test
        @DisplayName("When category is empty then all products are returned")
        void shouldReturnAllProducts_whenCategoryIsEmpty() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, Arrays.asList(), null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements())
            );
        }

        @Test
        @DisplayName("When no parameters are provided then all products are returned")
        void shouldReturnAllProducts_whenNoParametersAreProvided() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, null, null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements())
            );
        }

        @Test
        @DisplayName("When partial name match occurs then all matching products are returned")
        void shouldReturnMatchingProducts_whenPartialNameMatchOccurs() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, "Test", null, null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements()),
                    () -> assertTrue(results.getContent().stream()
                            .allMatch(p -> p.getName().contains("Test")))
            );
        }

        @Test
        @DisplayName("When sorting is applied then results are sorted accordingly")
        void shouldReturnSortedResults_whenSortingIsApplied() {
            repository.save(testProduct1);
            repository.save(testProduct2);
            repository.save(testProduct3);
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<Product> results = repository.findByNameOrCategoryOrQuantityInStock(
                    pageable, null, null, null);
            assertAll(
                    () -> assertNotNull(results),
                    () -> assertEquals(3, results.getTotalElements()),
                    () -> {
                        List<Product> content = results.getContent();
                        assertEquals("Test Keyboard", content.get(0).getName());
                        assertEquals("Test Laptop", content.get(1).getName());
                        assertEquals("Test Mouse", content.get(2).getName());
                    }
            );
        }

        @Test
        @DisplayName("When filtering with all parameters null or empty then all products are returned")
        void shouldReturnAllProducts_whenFilteringWithAllParametersNullOrEmpty() {
            repository.save(testProduct1);
            Pageable pageable = PageRequest.of(0, 10);
            assertAll(
                    () -> assertEquals(1, repository.findByNameOrCategoryOrQuantityInStock(pageable, null, null, null).getTotalElements()),
                    () -> assertEquals(1, repository.findByNameOrCategoryOrQuantityInStock(pageable, null, new ArrayList<>(), null).getTotalElements())
            );
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases")
    class ErrorHandlingAndEdgeCases {
        @Test
        @DisplayName("When finding by null ID then result is not present")
        void shouldReturnNotPresent_whenFindingByNullId() {
            Optional<Product> result = repository.findById(null);
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("When checking existence by null ID then result is false")
        void shouldReturnFalse_whenCheckingExistenceByNullId() {
            assertFalse(repository.existsById(null));
        }

        @Test
        @DisplayName("When deleting a null product then NullPointerException is thrown")
        void shouldThrowNullPointerException_whenDeletingNullProduct() {
            assertThrows(NullPointerException.class, () -> repository.delete(null));
        }

        @Test
        @DisplayName("When finding all by null ID list then NullPointerException is thrown")
        void shouldThrowNullPointerException_whenFindingAllByNullIdList() {
            assertThrows(NullPointerException.class, () -> repository.findAllById(null));
        }

        @Test
        @DisplayName("When deleting all by null ID list then NullPointerException is thrown")
        void shouldThrowNullPointerException_whenDeletingAllByNullIdList() {
            assertThrows(NullPointerException.class, () -> repository.deleteAllById(null));
        }

            }
}
