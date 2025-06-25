package inc.encora.inventory_manager.product.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Inventory metrics data for a specific category")
public class InventoryMetricDTO {
    @Schema(description = "Product category name", example = "Electronics")
    private String category;

    @Schema(description = "Total number of products in stock for this category", example = "150")
    private int totalProductsInStock;

    @Schema(description = "Total monetary value of products in stock for this category", example = "15750.50")
    private BigDecimal totalValueInStock;

    @Schema(description = "Average price of products in stock for this category", example = "105.00")
    private BigDecimal averagePriceInStock;
}
