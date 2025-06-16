package inc.encora.inventory_manager.product.mappers;

import inc.encora.inventory_manager.product.dtos.ProductDTO;
import inc.encora.inventory_manager.product.models.Product;

public class ProductMapper {
    public static Product toProductEntity(ProductDTO productdto) {
        return Product.builder()
                .name(productdto.getName())
                .category(productdto.getCategory())
                .unitPrice(productdto.getUnitPrice())
                .expirationDate(productdto.getExpirationDate())
                .quantityInStock(productdto.getQuantityInStock())
                .build();
    }
}
