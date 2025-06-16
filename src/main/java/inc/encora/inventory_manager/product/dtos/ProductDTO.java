package inc.encora.inventory_manager.product.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 120, min = 1, message = "Product name must be between {min} and {max} characters long.")
    private String name;

    @NotNull(message = "Product category is required")
    @NotBlank(message = "Product category cannot be empty")
    private String category;

    @DecimalMin(value = "0.1", message = "Unit price cannot be 0 or negative")
    private BigDecimal unitPrice;

    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;

    @Min(value = 0, message = "Quantity in stock cannot be negative")
    private Integer quantityInStock;
}