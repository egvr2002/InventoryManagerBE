package inc.encora.inventory_manager.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Product implements Serializable {
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;

    private String category;

    private BigDecimal unitPrice;

    private LocalDate expirationDate;

    private Integer quantityInStock;

    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Builder.Default
    private LocalDate updatedAt = LocalDate.now();

    @JsonIgnore
    public BigDecimal getProductValueInStock() {
        return unitPrice.multiply(new BigDecimal(quantityInStock));
    }
}
