package inc.encora.inventory_manager.common.seed;

import inc.encora.inventory_manager.product.models.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ProductSeed {
    public static Map<String, Product> createProductsSeed(int size) {
        Map<String, Product> products = new HashMap<>();

        String[] productNames = {
                "Laptop Pro", "Gaming Mouse X", "Ergonomic Keyboard", "4K Ultra Monitor",
                "HD Webcam Pro", "Wireless Headphones", "Studio Microphone", "Laser Printer 3000",
                "Dual-Band Router", "High-Speed SSD", "Smartphone Elite", "Tablet Lite",
                "Smartwatch Gear", "Mini Drone Pro", "VR Headset X1", "Portable HDD",
                "Super USB Drive", "E-Reader Oasis", "Compact Projector", "Bluetooth Speaker"
        };
        String[] categories = {
                "Electronics", "Peripherals", "Computing", "Mobile Devices",
                "Storage", "Audio", "Visual", "Networking"
        };

        String[] brands = {
                "TechCo", "GigaGear", "Innovate", "VisionPro", "SoundWave",
                "DataSwift", "MobileTech", "HomeGadget", "FutureLink", "Aura"
        };

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < size; i++) {
            String name = productNames[i % productNames.length] + " (" + brands[i % brands.length] + ")";
            String category = categories[i % categories.length];
            BigDecimal unitPrice = BigDecimal.valueOf(random.nextDouble(50.00, 1500.00)).setScale(2, RoundingMode.HALF_UP);
            LocalDate expirationDate = LocalDate.now().plusMonths(random.nextInt(1, 37)); // Expires within 1 to 36 months
            Integer quantityInStock = random.nextInt(1, 200);

            Product product = Product.builder()
                    .name(name)
                    .category(category)
                    .unitPrice(unitPrice)
                    .expirationDate(expirationDate)
                    .quantityInStock(quantityInStock)
                    .build();

            products.put(product.getId(), product);
        }

        return products;
    }
}
