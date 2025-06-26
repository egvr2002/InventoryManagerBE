package inc.encora.inventory_manager.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for creating and updating products")
public class ProductDTO {
    @Schema(description = "Product name", example = "Gaming Mouse X Pro", minLength = 1, maxLength = 120)
    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name cannot be empty")
    @Size(max = 120, min = 1, message = "Product name must be between {min} and {max} characters long.")
    private String name;

    @Schema(description = "Product category", example = "Peripherals")
    @NotNull(message = "Product category is required")
    @NotBlank(message = "Product category cannot be empty")
    private String category;

    @Schema(description = "Unit price of the product", example = "79.99", minimum = "0.1")
    @DecimalMin(value = "0.1", message = "Unit price cannot be 0 or negative")
    private BigDecimal unitPrice;

    @Schema(description = "Product expiration date", example = "2027-03-15", type = "string", format = "date")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;

    @Schema(description = "Quantity available in stock", example = "50", minimum = "0")
    @Min(value = 0, message = "Quantity in stock cannot be negative")
    private Integer quantityInStock;
}