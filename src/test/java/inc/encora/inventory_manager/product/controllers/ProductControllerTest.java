package inc.encora.inventory_manager.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.dtos.InventoryMetricDTO;
import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDTO validProductDTO;
    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        validProductDTO = ProductDTO.builder()
                .name("Gaming Mouse X Pro")
                .category("Peripherals")
                .unitPrice(new BigDecimal("79.99"))
                .expirationDate(LocalDate.now().plusYears(2))
                .quantityInStock(50)
                .build();

        testProduct1 = Product.builder()
                .id("test-id-1")
                .name("Gaming Mouse X Pro")
                .category("Peripherals")
                .unitPrice(new BigDecimal("79.99"))
                .expirationDate(LocalDate.now().plusYears(2))
                .quantityInStock(50)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        testProduct2 = Product.builder()
                .id("test-id-2")
                .name("Wireless Keyboard")
                .category("Peripherals")
                .unitPrice(new BigDecimal("129.99"))
                .expirationDate(LocalDate.now().plusYears(1))
                .quantityInStock(0)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        testProduct3 = Product.builder()
                .id("test-id-3")
                .name("USB Cable")
                .category("Accessories")
                .unitPrice(new BigDecimal("9.99"))
                .expirationDate(LocalDate.now().plusMonths(6))
                .quantityInStock(100)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/products - Find All Products")
    class FindAllProducts {

        @Test
        @DisplayName("Should return 200 OK with paginated products list when products exist")
        void shouldReturnOkWithPaginatedList_whenProductsExist() throws Exception {
            Page<Product> productPage = new PageImpl<>(
                    Arrays.asList(testProduct1, testProduct2),
                    PageRequest.of(0, 10, Sort.by("name").ascending()),
                    2
            );
            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content", hasSize(2)))
                    .andExpect(jsonPath("$.data.page.totalElements").value(2));

            verify(productService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 200 OK with empty page when no products exist")
        void shouldReturnOkWithEmptyPage_whenNoProductsExist() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(
                    Collections.emptyList(),
                    PageRequest.of(0, 10, Sort.by("name").ascending()),
                    0
            );
            when(productService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.data.content", hasSize(0)))
                    .andExpect(jsonPath("$.data.page.totalElements").value(0));

            verify(productService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should apply default pagination when no parameters provided")
        void shouldApplyDefaultPagination_whenNoParametersProvided() throws Exception {
            Page<Product> productPage = new PageImpl<>(Collections.emptyList());
            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).findAll(argThat(pageable ->
                    pageable.getSort().getOrderFor("name") != null &&
                            pageable.getSort().getOrderFor("name").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Should apply custom pagination parameters correctly")
        void shouldApplyCustomPagination_whenParametersProvided() throws Exception {
            Page<Product> productPage = new PageImpl<>(Collections.emptyList());
            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/products")
                            .param("page", "1")
                            .param("size", "5")
                            .param("sort", "unitPrice,desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).findAll(argThat(pageable ->
                    pageable.getPageNumber() == 1 &&
                            pageable.getPageSize() == 5
            ));
        }

        @Test
        @DisplayName("Should return correct response structure with ApiResponseDTO wrapper")
        void shouldReturnCorrectResponseStructure_withApiResponseDTOWrapper() throws Exception {
            Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct1));
            when(productService.findAll(any(Pageable.class))).thenReturn(productPage);

            mockMvc.perform(get("/api/products")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/products - Create Product")
    class CreateProduct {

        @Test
        @DisplayName("Should return 201 CREATED with product data when valid ProductDTO is provided")
        void shouldReturnCreatedWithProductData_whenValidDTOProvided() throws Exception {
            when(productService.save(any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.message").value("Product saved successfully"))
                    .andExpect(jsonPath("$.data.id").value("test-id-1"))
                    .andExpect(jsonPath("$.data.name").value("Gaming Mouse X Pro"))
                    .andExpect(jsonPath("$.data.category").value("Peripherals"));

            verify(productService, times(1)).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when product name is null")
        void shouldReturnBadRequest_whenProductNameIsNull() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name(null)
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when product name is blank")
        void shouldReturnBadRequest_whenProductNameIsBlank() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("   ")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when category is null")
        void shouldReturnBadRequest_whenCategoryIsNull() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category(null)
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when price is negative")
        void shouldReturnBadRequest_whenPriceIsNegative() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("-10.00"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when quantity is negative")
        void shouldReturnBadRequest_whenQuantityIsNegative() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(-5)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when expiration date is in the past")
        void shouldReturnBadRequest_whenExpirationDateIsInThePast() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().minusDays(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when name exceeds max length")
        void shouldReturnBadRequest_whenNameExceedsMaxLength() throws Exception {
            String longName = "a".repeat(121);
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name(longName)
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should accept product with zero quantity in stock")
        void shouldAcceptProduct_withZeroQuantity() throws Exception {
            ProductDTO validDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(0)
                    .build();

            when(productService.save(any(ProductDTO.class))).thenReturn(testProduct2);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO)))
                    .andExpect(status().isCreated());

            verify(productService, times(1)).save(any(ProductDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/products/search - Search Products")
    class SearchProducts {

        @Test
        @DisplayName("Should return 200 OK with filtered results when name parameter is provided")
        void shouldReturnFilteredResults_whenNameProvided() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Collections.singletonList(testProduct1));
            when(productService.search(any(Pageable.class), eq("Gaming"), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("name", "Gaming")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].name").value("Gaming Mouse X Pro"));

            verify(productService).search(any(Pageable.class), eq("Gaming"), isNull(), any(AvailabilityStatus.class));
        }

        @Test
        @DisplayName("Should return 200 OK with all products when no search parameters provided")
        void shouldReturnAllProducts_whenNoSearchParametersProvided() throws Exception {
            Page<Product> allProducts = new PageImpl<>(Arrays.asList(testProduct1, testProduct2, testProduct3));
            when(productService.search(any(Pageable.class), anyString(), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(allProducts);

            mockMvc.perform(get("/api/products/search")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(3)));
        }

        @Test
        @DisplayName("Should filter by single category correctly")
        void shouldFilterCorrectly_bySingleCategory() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Arrays.asList(testProduct1, testProduct2));
            when(productService.search(any(Pageable.class), anyString(), eq(Collections.singletonList("Peripherals")), any(AvailabilityStatus.class)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("category", "Peripherals")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(2)));

            verify(productService).search(any(Pageable.class), anyString(), eq(Collections.singletonList("Peripherals")), any(AvailabilityStatus.class));
        }

        @Test
        @DisplayName("Should filter by multiple categories correctly")
        void shouldFilterCorrectly_byMultipleCategories() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Arrays.asList(testProduct1, testProduct2, testProduct3));
            List<String> categories = Arrays.asList("Peripherals", "Accessories");
            when(productService.search(any(Pageable.class), anyString(), eq(categories), any(AvailabilityStatus.class)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("category", "Peripherals")
                            .param("category", "Accessories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(3)));
        }

        @Test
        @DisplayName("Should filter by IN_STOCK availability status")
        void shouldFilterCorrectly_byInStockAvailability() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Arrays.asList(testProduct1, testProduct3));
            when(productService.search(any(Pageable.class), anyString(), isNull(), eq(AvailabilityStatus.IN_STOCK)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("availability", "in_stock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(2)));

            verify(productService).search(any(Pageable.class), anyString(), isNull(), eq(AvailabilityStatus.IN_STOCK));
        }

        @Test
        @DisplayName("Should filter by OUT_OF_STOCK availability status")
        void shouldFilterCorrectly_byOutOfStockAvailability() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Collections.singletonList(testProduct2));
            when(productService.search(any(Pageable.class), anyString(), isNull(), eq(AvailabilityStatus.OUT_OF_STOCK)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("availability", "out_of_stock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].quantityInStock").value(0));
        }

        @Test
        @DisplayName("Should filter by ALL availability status and return all products")
        void shouldReturnAllProducts_whenFilteringByAllAvailability() throws Exception {
            Page<Product> allProducts = new PageImpl<>(Arrays.asList(testProduct1, testProduct2, testProduct3));
            when(productService.search(any(Pageable.class), anyString(), isNull(), eq(AvailabilityStatus.ALL)))
                    .thenReturn(allProducts);

            mockMvc.perform(get("/api/products/search")
                            .param("availability", "all")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(3)));
        }

        @Test
        @DisplayName("Should combine name, category, and availability filters correctly")
        void shouldCombineFiltersCorrectly_whenMultipleFiltersProvided() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Collections.singletonList(testProduct1));
            when(productService.search(any(Pageable.class), eq("Gaming"), eq(Collections.singletonList("Peripherals")), eq(AvailabilityStatus.IN_STOCK)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("name", "Gaming")
                            .param("category", "Peripherals")
                            .param("availability", "in_stock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should handle empty string name parameter and return all products")
        void shouldReturnAllProducts_whenNameIsEmptyString() throws Exception {
            Page<Product> allProducts = new PageImpl<>(Arrays.asList(testProduct1, testProduct2, testProduct3));
            when(productService.search(any(Pageable.class), eq(""), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(allProducts);

            mockMvc.perform(get("/api/products/search")
                            .param("name", "")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should apply pagination to search results")
        void shouldApplyPagination_toSearchResults() throws Exception {
            Page<Product> searchResults = new PageImpl<>(
                    Collections.singletonList(testProduct1),
                    PageRequest.of(0, 1),
                    3
            );
            when(productService.search(any(Pageable.class), anyString(), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("page", "0")
                            .param("size", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page.totalElements").value(3))
                    .andExpect(jsonPath("$.data.content", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id} - Update Product")
    class UpdateProduct {

        @Test
        @DisplayName("Should return 200 OK with updated product when valid id and ProductDTO provided")
        void shouldReturnOkWithUpdatedProduct_whenValidIdAndDTOProvided() throws Exception {
            Product updatedProduct = Product.builder()
                    .id("test-id-1")
                    .name("Updated Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("89.99"))
                    .expirationDate(LocalDate.now().plusYears(2))
                    .quantityInStock(40)
                    .build();

            when(productService.update(eq("test-id-1"), any(ProductDTO.class))).thenReturn(updatedProduct);

            mockMvc.perform(put("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Product updated successfully"))
                    .andExpect(jsonPath("$.data.id").value("test-id-1"))
                    .andExpect(jsonPath("$.data.name").value("Updated Gaming Mouse"));

            verify(productService, times(1)).update(eq("test-id-1"), any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when ProductDTO validation fails")
        void shouldReturnBadRequest_whenDTOValidationFails() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(put("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).update(anyString(), any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should not allow updating with negative price")
        void shouldNotAllowUpdate_withNegativePrice() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("-79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(50)
                    .build();

            mockMvc.perform(put("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).update(anyString(), any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should not allow updating with negative quantity")
        void shouldNotAllowUpdate_withNegativeQuantity() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Gaming Mouse")
                    .category("Peripherals")
                    .unitPrice(new BigDecimal("79.99"))
                    .expirationDate(LocalDate.now().plusYears(1))
                    .quantityInStock(-10)
                    .build();

            mockMvc.perform(put("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).update(anyString(), any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should correctly pass id and DTO to service layer")
        void shouldCorrectlyPassIdAndDTO_toServiceLayer() throws Exception {
            when(productService.update(eq("specific-id"), any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(put("/api/products/specific-id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductDTO)))
                    .andExpect(status().isOk());

            verify(productService).update(eq("specific-id"), any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should return updated product data in response")
        void shouldReturnUpdatedProductData_inResponse() throws Exception {
            when(productService.update(anyString(), any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(put("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data.id").value("test-id-1"))
                    .andExpect(jsonPath("$.data.name").exists())
                    .andExpect(jsonPath("$.data.category").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id} - Delete Product")
    class DeleteProduct {

        @Test
        @DisplayName("Should return 200 OK when product is successfully deleted")
        void shouldReturnOk_whenProductSuccessfullyDeleted() throws Exception {
            doNothing().when(productService).deleteById("test-id-1");

            mockMvc.perform(delete("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Product deleted successfully"))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(productService, times(1)).deleteById("test-id-1");
        }

        @Test
        @DisplayName("Should call productService.deleteById with correct id")
        void shouldCallServiceDeleteById_withCorrectId() throws Exception {
            doNothing().when(productService).deleteById("specific-id-123");

            mockMvc.perform(delete("/api/products/specific-id-123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).deleteById("specific-id-123");
        }

        @Test
        @DisplayName("Should return null data in response body")
        void shouldReturnNullData_inResponseBody() throws Exception {
            doNothing().when(productService).deleteById(anyString());

            mockMvc.perform(delete("/api/products/test-id-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/products/{id}/outofstock - Mark Product Out of Stock")
    class MarkProductOutOfStock {

        @Test
        @DisplayName("Should return 200 OK when product is successfully marked out of stock")
        void shouldReturnOk_whenProductMarkedOutOfStock() throws Exception {
            doNothing().when(productService).markProductOutOfStock("test-id-1");

            mockMvc.perform(post("/api/products/test-id-1/outofstock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Product marked as out of stock successfully"))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(productService, times(1)).markProductOutOfStock("test-id-1");
        }

        @Test
        @DisplayName("Should call productService.markProductOutOfStock with correct id")
        void shouldCallServiceMarkOutOfStock_withCorrectId() throws Exception {
            doNothing().when(productService).markProductOutOfStock("specific-id");

            mockMvc.perform(post("/api/products/specific-id/outofstock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).markProductOutOfStock("specific-id");
        }
    }

    @Nested
    @DisplayName("POST /api/products/{id}/instock - Mark Product In Stock")
    class MarkProductInStock {

        @Test
        @DisplayName("Should return 200 OK when product is successfully marked in stock")
        void shouldReturnOk_whenProductMarkedInStock() throws Exception {
            doNothing().when(productService).markProductInStock("test-id-1");

            mockMvc.perform(post("/api/products/test-id-1/instock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Product marked as in stock successfully"))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(productService, times(1)).markProductInStock("test-id-1");
        }

        @Test
        @DisplayName("Should call productService.markProductInStock with correct id")
        void shouldCallServiceMarkInStock_withCorrectId() throws Exception {
            doNothing().when(productService).markProductInStock("specific-id");

            mockMvc.perform(post("/api/products/specific-id/instock")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).markProductInStock("specific-id");
        }
    }

    @Nested
    @DisplayName("GET /api/products/categories - Find All Categories")
    class FindAllCategories {

        @Test
        @DisplayName("Should return 200 OK with list of unique categories")
        void shouldReturnOkWithUniqueCategories() throws Exception {
            List<String> categories = Arrays.asList("Peripherals", "Accessories", "Electronics");
            when(productService.findAllCategories()).thenReturn(categories);

            mockMvc.perform(get("/api/products/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Categories retrieved successfully"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(3)))
                    .andExpect(jsonPath("$.data[0]").value("Peripherals"))
                    .andExpect(jsonPath("$.data[1]").value("Accessories"))
                    .andExpect(jsonPath("$.data[2]").value("Electronics"));

            verify(productService, times(1)).findAllCategories();
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void shouldReturnEmptyList_whenNoProductsExist() throws Exception {
            when(productService.findAllCategories()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("Should return correct response structure")
        void shouldReturnCorrectResponseStructure() throws Exception {
            when(productService.findAllCategories()).thenReturn(Collections.singletonList("Electronics"));

            mockMvc.perform(get("/api/products/categories")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.data").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/products/metrics - Get Inventory Metrics")
    class GetInventoryMetrics {

        @Test
        @DisplayName("Should return 200 OK with inventory metrics grouped by category")
        void shouldReturnOkWithInventoryMetrics() throws Exception {
            List<InventoryMetricDTO> metrics = Arrays.asList(
                    InventoryMetricDTO.builder()
                            .category("Peripherals")
                            .totalProductsInStock(2)
                            .totalValueInStock(new BigDecimal("209.98"))
                            .averagePriceInStock(new BigDecimal("104.99"))
                            .build(),
                    InventoryMetricDTO.builder()
                            .category("Accessories")
                            .totalProductsInStock(1)
                            .totalValueInStock(new BigDecimal("999.00"))
                            .averagePriceInStock(new BigDecimal("999.00"))
                            .build()
            );
            when(productService.getInventoryMetrics()).thenReturn(metrics);

            mockMvc.perform(get("/api/products/metrics")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("Ok"))
                    .andExpect(jsonPath("$.statusCode").value(200))
                    .andExpect(jsonPath("$.message").value("Inventory metrics retrieved successfully"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].category").value("Peripherals"))
                    .andExpect(jsonPath("$.data[0].totalProductsInStock").value(2))
                    .andExpect(jsonPath("$.data[1].category").value("Accessories"));

            verify(productService, times(1)).getInventoryMetrics();
        }

        @Test
        @DisplayName("Should handle empty inventory correctly")
        void shouldHandleEmptyInventory() throws Exception {
            when(productService.getInventoryMetrics()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products/metrics")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("Should return correct metrics structure")
        void shouldReturnCorrectMetricsStructure() throws Exception {
            List<InventoryMetricDTO> metrics = Collections.singletonList(
                    InventoryMetricDTO.builder()
                            .category("Electronics")
                            .totalProductsInStock(5)
                            .totalValueInStock(new BigDecimal("1500.00"))
                            .averagePriceInStock(new BigDecimal("300.00"))
                            .build()
            );
            when(productService.getInventoryMetrics()).thenReturn(metrics);

            mockMvc.perform(get("/api/products/metrics")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].category").exists())
                    .andExpect(jsonPath("$.data[0].totalProductsInStock").exists())
                    .andExpect(jsonPath("$.data[0].totalValueInStock").exists())
                    .andExpect(jsonPath("$.data[0].averagePriceInStock").exists());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle special characters in search parameters")
        void shouldHandleSpecialCharacters_inSearchParameters() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productService.search(any(Pageable.class), anyString(), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/api/products/search")
                            .param("name", "Test@#$%^&*()")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(productService).search(any(Pageable.class), eq("Test@#$%^&*()"), isNull(), any(AvailabilityStatus.class));
        }

        @Test
        @DisplayName("Should handle very large pagination requests")
        void shouldHandleVeryLargePagination() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/products")
                            .param("size", "1000")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle null category list in search")
        void shouldHandleNullCategoryList_inSearch() throws Exception {
            Page<Product> searchResults = new PageImpl<>(Collections.emptyList());
            when(productService.search(any(Pageable.class), anyString(), isNull(), any(AvailabilityStatus.class)))
                    .thenReturn(searchResults);

            mockMvc.perform(get("/api/products/search")
                            .param("name", "Test")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle empty request body gracefully")
        void shouldHandleEmptyRequestBody() throws Exception {
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should validate all required fields in ProductDTO")
        void shouldValidateAllRequiredFields_inProductDTO() throws Exception {
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).save(any(ProductDTO.class));
        }

        @Test
        @DisplayName("Should handle price with minimum valid value")
        void shouldHandlePrice_withMinimumValidValue() throws Exception {
            ProductDTO validDTO = ProductDTO.builder()
                    .name("Test Product")
                    .category("Test")
                    .unitPrice(new BigDecimal("0.1"))
                    .expirationDate(LocalDate.now().plusDays(1))
                    .quantityInStock(1)
                    .build();

            when(productService.save(any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should reject price below minimum valid value")
        void shouldRejectPrice_belowMinimumValidValue() throws Exception {
            ProductDTO invalidDTO = ProductDTO.builder()
                    .name("Test Product")
                    .category("Test")
                    .unitPrice(new BigDecimal("0.09"))
                    .expirationDate(LocalDate.now().plusDays(1))
                    .quantityInStock(1)
                    .build();

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle product name at maximum valid length")
        void shouldHandleProductName_atMaximumValidLength() throws Exception {
            String maxLengthName = "a".repeat(120);
            ProductDTO validDTO = ProductDTO.builder()
                    .name(maxLengthName)
                    .category("Test")
                    .unitPrice(new BigDecimal("10.00"))
                    .expirationDate(LocalDate.now().plusDays(1))
                    .quantityInStock(1)
                    .build();

            when(productService.save(any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle expiration date as current date")
        void shouldHandleExpirationDate_asCurrentDate() throws Exception {
            ProductDTO validDTO = ProductDTO.builder()
                    .name("Test Product")
                    .category("Test")
                    .unitPrice(new BigDecimal("10.00"))
                    .expirationDate(LocalDate.now())
                    .quantityInStock(1)
                    .build();

            when(productService.save(any(ProductDTO.class))).thenReturn(testProduct1);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validDTO)))
                    .andExpect(status().isCreated());
        }
    }
}
