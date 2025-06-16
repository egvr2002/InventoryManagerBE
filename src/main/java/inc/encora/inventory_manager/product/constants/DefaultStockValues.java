package inc.encora.inventory_manager.product.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultStockValues {
    OUT_OF_STOCK(0),
    RESTORED_STOCK(10);

    private final int value;
}
