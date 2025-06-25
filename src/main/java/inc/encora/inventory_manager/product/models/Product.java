package inc.encora.inventory_manager.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Schema(description = "Product entity representing an inventory item")
public class Product implements Serializable {
    @Schema(description = "Unique product identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Schema(description = "Product name", example = "Gaming Mouse X Pro (GigaGear)")
    private String name;

    @Schema(description = "Product category", example = "Peripherals")
    private String category;

    @Schema(description = "Unit price of the product", example = "79.99")
    private BigDecimal unitPrice;

    @Schema(description = "Product expiration date", example = "2027-03-15", type = "string", format = "date")
    private LocalDate expirationDate;

    @Schema(description = "Quantity available in stock", example = "50")
    private Integer quantityInStock;

    @Schema(description = "Date when the product was created", example = "2025-06-25", type = "string", format = "date")
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Schema(description = "Date when the product was last updated", example = "2025-06-25", type = "string", format = "date")
    @Builder.Default
    private LocalDate updatedAt = LocalDate.now();

    @JsonIgnore
    public BigDecimal getProductValueInStock() {
        return unitPrice.multiply(new BigDecimal(quantityInStock));
    }
}
