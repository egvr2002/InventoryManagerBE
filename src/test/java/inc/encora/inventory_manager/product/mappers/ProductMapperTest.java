package inc.encora.inventory_manager.product.mappers;

import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductMapper Tests")
class ProductMapperTest {

    @Test
    @DisplayName("Should map valid ProductDTO to Product entity with all fields")
    void shouldMapValidProductDtoToProductEntity() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Gaming Mouse X Pro")
                .category("Peripherals")
                .unitPrice(new BigDecimal("79.99"))
                .expirationDate(LocalDate.of(2027, 3, 15))
                .quantityInStock(50)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Gaming Mouse X Pro", product.getName(), "Product name should match");
        assertEquals("Peripherals", product.getCategory(), "Product category should match");
        assertEquals(new BigDecimal("79.99"), product.getUnitPrice(), "Product unit price should match");
        assertEquals(LocalDate.of(2027, 3, 15), product.getExpirationDate(), "Product expiration date should match");
        assertEquals(50, product.getQuantityInStock(), "Product quantity in stock should match");
    }

    @Test
    @DisplayName("Should map ProductDTO with minimum valid values")
    void shouldMapProductDtoWithMinimumValidValues() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("A")
                .category("Electronics")
                .unitPrice(new BigDecimal("0.1"))
                .expirationDate(LocalDate.now())
                .quantityInStock(0)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("A", product.getName(), "Product name should match minimum value");
        assertEquals("Electronics", product.getCategory(), "Product category should match");
        assertEquals(new BigDecimal("0.1"), product.getUnitPrice(), "Product unit price should match minimum value");
        assertEquals(LocalDate.now(), product.getExpirationDate(), "Product expiration date should match today");
        assertEquals(0, product.getQuantityInStock(), "Product quantity in stock should match minimum value");
    }

    @Test
    @DisplayName("Should map ProductDTO with maximum valid values")
    void shouldMapProductDtoWithMaximumValidValues() {
        String maxLengthName = "A".repeat(120);
        ProductDTO productDTO = ProductDTO.builder()
                .name(maxLengthName)
                .category("Electronics & Gadgets")
                .unitPrice(new BigDecimal("999999.99"))
                .expirationDate(LocalDate.of(2099, 12, 31))
                .quantityInStock(Integer.MAX_VALUE)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(maxLengthName, product.getName(), "Product name should match maximum length value");
        assertEquals(120, product.getName().length(), "Product name length should be 120");
        assertEquals("Electronics & Gadgets", product.getCategory(), "Product category should match");
        assertEquals(new BigDecimal("999999.99"), product.getUnitPrice(), "Product unit price should match large value");
        assertEquals(LocalDate.of(2099, 12, 31), product.getExpirationDate(), "Product expiration date should match far future date");
        assertEquals(Integer.MAX_VALUE, product.getQuantityInStock(), "Product quantity in stock should match maximum integer value");
    }

    @Test
    @DisplayName("Should preserve BigDecimal precision during mapping")
    void shouldPreserveBigDecimalPrecision() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Precision Test Product")
                .category("Test")
                .unitPrice(new BigDecimal("99.99"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(0, new BigDecimal("99.99").compareTo(product.getUnitPrice()),
                "Product unit price should preserve decimal precision");
        assertNotEquals(new BigDecimal("99.989999"), product.getUnitPrice(),
                "Product unit price should not have precision errors");
    }

    @Test
    @DisplayName("Should generate new Product ID on each mapping")
    void shouldGenerateNewProductIdOnEachMapping() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Test")
                .unitPrice(new BigDecimal("10.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(5)
                .build();

        Product product1 = ProductMapper.toProductEntity(productDTO);
        Product product2 = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product1.getId(), "First product ID should not be null");
        assertNotNull(product2.getId(), "Second product ID should not be null");
        assertNotEquals(product1.getId(), product2.getId(),
                "Each mapped product should have a unique ID");
    }

    @Test
    @DisplayName("Should set default createdAt and updatedAt timestamps")
    void shouldSetDefaultTimestamps() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Test")
                .unitPrice(new BigDecimal("10.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(5)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product.getCreatedAt(), "Product createdAt should not be null");
        assertNotNull(product.getUpdatedAt(), "Product updatedAt should not be null");
        assertEquals(LocalDate.now(), product.getCreatedAt(),
                "Product createdAt should be set to current date");
        assertEquals(LocalDate.now(), product.getUpdatedAt(),
                "Product updatedAt should be set to current date");
    }

    @Test
    @DisplayName("Should map ProductDTO with special characters in name")
    void shouldMapProductDtoWithSpecialCharactersInName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product & Co. - Special™ Edition (v2.0)")
                .category("Electronics")
                .unitPrice(new BigDecimal("49.99"))
                .expirationDate(LocalDate.of(2026, 6, 1))
                .quantityInStock(25)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Product & Co. - Special™ Edition (v2.0)", product.getName(),
                "Product name with special characters should be preserved");
    }

    @Test
    @DisplayName("Should not modify the source ProductDTO during mapping")
    void shouldNotModifySourceProductDto() {
        String originalName = "Original Product";
        String originalCategory = "Original Category";
        BigDecimal originalPrice = new BigDecimal("59.99");
        LocalDate originalDate = LocalDate.of(2026, 5, 1);
        Integer originalQuantity = 100;

        ProductDTO productDTO = ProductDTO.builder()
                .name(originalName)
                .category(originalCategory)
                .unitPrice(originalPrice)
                .expirationDate(originalDate)
                .quantityInStock(originalQuantity)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertEquals(originalName, productDTO.getName(), "ProductDTO name should not be modified");
        assertEquals(originalCategory, productDTO.getCategory(), "ProductDTO category should not be modified");
        assertEquals(originalPrice, productDTO.getUnitPrice(), "ProductDTO unit price should not be modified");
        assertEquals(originalDate, productDTO.getExpirationDate(), "ProductDTO expiration date should not be modified");
        assertEquals(originalQuantity, productDTO.getQuantityInStock(), "ProductDTO quantity should not be modified");
    }

    @Test
    @DisplayName("Should throw NullPointerException when ProductDTO is null")
    void shouldThrowExceptionWhenProductDtoIsNull() {
        assertThrows(NullPointerException.class, () -> ProductMapper.toProductEntity(null),
                "Should throw NullPointerException when ProductDTO is null");
    }

    @Test
    @DisplayName("Should map ProductDTO with null unitPrice")
    void shouldMapProductDtoWithNullUnitPrice() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product Without Price")
                .category("Electronics")
                .unitPrice(null)
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertNull(product.getUnitPrice(), "Product unit price should be null");
        assertEquals("Product Without Price", product.getName(), "Product name should match");
    }

    @Test
    @DisplayName("Should map ProductDTO with null expirationDate")
    void shouldMapProductDtoWithNullExpirationDate() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product Without Expiration")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(null)
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertNull(product.getExpirationDate(), "Product expiration date should be null");
        assertEquals("Product Without Expiration", product.getName(), "Product name should match");
    }

    @Test
    @DisplayName("Should map ProductDTO with null quantityInStock")
    void shouldMapProductDtoWithNullQuantityInStock() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product Without Quantity")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(null)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertNull(product.getQuantityInStock(), "Product quantity should be null");
        assertEquals("Product Without Quantity", product.getName(), "Product name should match");
    }

    @Test
    @DisplayName("Should map ProductDTO with all optional fields null")
    void shouldMapProductDtoWithAllOptionalFieldsNull() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Minimal Product")
                .category("Electronics")
                .unitPrice(null)
                .expirationDate(null)
                .quantityInStock(null)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Minimal Product", product.getName(), "Product name should match");
        assertEquals("Electronics", product.getCategory(), "Product category should match");
        assertNull(product.getUnitPrice(), "Product unit price should be null");
        assertNull(product.getExpirationDate(), "Product expiration date should be null");
        assertNull(product.getQuantityInStock(), "Product quantity should be null");
    }

    @Test
    @DisplayName("Should map ProductDTO with empty string name")
    void shouldMapProductDtoWithEmptyStringName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("", product.getName(), "Product name should be empty string");
    }

    @Test
    @DisplayName("Should map ProductDTO with empty string category")
    void shouldMapProductDtoWithEmptyStringCategory() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("", product.getCategory(), "Product category should be empty string");
    }

    @Test
    @DisplayName("Should map ProductDTO with whitespace-only name")
    void shouldMapProductDtoWithWhitespaceOnlyName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("   ")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("   ", product.getName(), "Product name should preserve whitespace");
    }

    @Test
    @DisplayName("Should map ProductDTO with leading and trailing whitespace in name")
    void shouldMapProductDtoWithLeadingAndTrailingWhitespace() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("  Product Name  ")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("  Product Name  ", product.getName(), "Product name should preserve leading and trailing whitespace");
    }

    @Test
    @DisplayName("Should map ProductDTO with Unicode characters in name")
    void shouldMapProductDtoWithUnicodeCharacters() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("产品名称 Продукт Προϊόν 製品")
                .category("International")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("产品名称 Продукт Προϊόν 製品", product.getName(), "Product name should preserve Unicode characters");
    }

    @Test
    @DisplayName("Should map ProductDTO with emoji in name")
    void shouldMapProductDtoWithEmojiInName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Cool Product 😎🔥")
                .category("Electronics")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Cool Product 😎🔥", product.getName(), "Product name should preserve emoji");
    }

    @Test
    @DisplayName("Should map ProductDTO with very high precision BigDecimal")
    void shouldMapProductDtoWithHighPrecisionBigDecimal() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Precision Product")
                .category("Test")
                .unitPrice(new BigDecimal("99.999999999999"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(0, new BigDecimal("99.999999999999").compareTo(product.getUnitPrice()),
                "Product unit price should preserve high precision");
    }

    @Test
    @DisplayName("Should map ProductDTO with BigDecimal having trailing zeros")
    void shouldMapProductDtoWithBigDecimalTrailingZeros() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Test Product")
                .category("Test")
                .unitPrice(new BigDecimal("100.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(0, new BigDecimal("100.00").compareTo(product.getUnitPrice()),
                "Product unit price should preserve value with trailing zeros");
    }

    @Test
    @DisplayName("Should map ProductDTO with zero BigDecimal")
    void shouldMapProductDtoWithZeroBigDecimal() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Free Product")
                .category("Test")
                .unitPrice(BigDecimal.ZERO)
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(0, BigDecimal.ZERO.compareTo(product.getUnitPrice()),
                "Product unit price should be zero");
    }

    @Test
    @DisplayName("Should map ProductDTO with negative quantityInStock")
    void shouldMapProductDtoWithNegativeQuantity() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Negative Stock Product")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(-10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(-10, product.getQuantityInStock(), "Product quantity should be negative");
    }

    @Test
    @DisplayName("Should map ProductDTO with past expiration date")
    void shouldMapProductDtoWithPastExpirationDate() {
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        ProductDTO productDTO = ProductDTO.builder()
                .name("Expired Product")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(pastDate)
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(pastDate, product.getExpirationDate(), "Product expiration date should be in the past");
    }

    @Test
    @DisplayName("Should map ProductDTO with today as expiration date")
    void shouldMapProductDtoWithTodayAsExpirationDate() {
        LocalDate today = LocalDate.now();
        ProductDTO productDTO = ProductDTO.builder()
                .name("Expiring Today Product")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(today)
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(today, product.getExpirationDate(), "Product expiration date should be today");
    }

    @Test
    @DisplayName("Should map ProductDTO with date at year boundary")
    void shouldMapProductDtoWithYearBoundaryDate() {
        LocalDate yearEnd = LocalDate.of(2025, 12, 31);
        ProductDTO productDTO = ProductDTO.builder()
                .name("Year End Product")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(yearEnd)
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(yearEnd, product.getExpirationDate(), "Product expiration date should be year boundary");
    }

    @Test
    @DisplayName("Should map ProductDTO with leap year date")
    void shouldMapProductDtoWithLeapYearDate() {
        LocalDate leapYearDate = LocalDate.of(2024, 2, 29);
        ProductDTO productDTO = ProductDTO.builder()
                .name("Leap Year Product")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(leapYearDate)
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(leapYearDate, product.getExpirationDate(), "Product expiration date should be leap year date");
    }

    @Test
    @DisplayName("Should map ProductDTO with very large BigDecimal")
    void shouldMapProductDtoWithVeryLargeBigDecimal() {
        BigDecimal largePrice = new BigDecimal("999999999999999999.99");
        ProductDTO productDTO = ProductDTO.builder()
                .name("Very Expensive Product")
                .category("Test")
                .unitPrice(largePrice)
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(1)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals(0, largePrice.compareTo(product.getUnitPrice()),
                "Product unit price should handle very large values");
    }

    @Test
    @DisplayName("Should map ProductDTO with newline characters in name")
    void shouldMapProductDtoWithNewlineInName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product\nWith\nNewlines")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Product\nWith\nNewlines", product.getName(), "Product name should preserve newline characters");
    }

    @Test
    @DisplayName("Should map ProductDTO with tab characters in name")
    void shouldMapProductDtoWithTabInName() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Product\tWith\tTabs")
                .category("Test")
                .unitPrice(new BigDecimal("50.00"))
                .expirationDate(LocalDate.of(2026, 1, 1))
                .quantityInStock(10)
                .build();

        Product product = ProductMapper.toProductEntity(productDTO);

        assertNotNull(product, "Product should not be null");
        assertEquals("Product\tWith\tTabs", product.getName(), "Product name should preserve tab characters");
    }
}
