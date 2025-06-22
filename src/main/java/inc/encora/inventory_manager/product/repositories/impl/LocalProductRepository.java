package inc.encora.inventory_manager.product.repositories.impl;

import inc.encora.inventory_manager.common.seed.ProductSeed;
import inc.encora.inventory_manager.common.utils.InMemoryRespositoryUtil;
import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LocalProductRepository implements ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

    // This is only for testing purposes in development
    public LocalProductRepository() {
        products.putAll(ProductSeed.createProductsSeed(20));
    }

    @Override
    @NonNull
    public <S extends Product> S save(@NonNull S entity) {
        products.put(entity.getId(), entity);
        return entity;
    }

    @Override
    @NonNull
    public <S extends Product> Iterable<S> saveAll(Iterable<S> entities) {
        for (Product entity : entities) {
            products.put(entity.getId(), entity);
        }
        return entities;
    }

    @Override
    @NonNull
    public Optional<Product> findById(@NonNull String s) {
        return Optional.ofNullable(products.get(s));
    }

    @Override
    public boolean existsById(@NonNull String s) {
        return products.containsKey(s);
    }

    @Override
    @NonNull
    public Iterable<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    @NonNull
    public Iterable<Product> findAllById(Iterable<String> strings) {
        Map<String, Product> productsFiltered = new HashMap<>();
        for (String id : strings) {
            Product product = products.get(id);
            if (product != null) {
                productsFiltered.put(product.getId(), product);
            }
        }

        return new ArrayList<>(productsFiltered.values());
    }

    @Override
    public long count() {
        return products.size();
    }

    @Override
    public void deleteById(@NonNull String s) {
        products.remove(s);
    }

    @Override
    public void delete(Product entity) {
        products.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        for (String id : strings) {
            products.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Product> entities) {
        for (Product entity : entities) {
            products.remove(entity.getId());
        }
    }

    @Override
    public void deleteAll() {
        products.clear();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        List<Product> allProducts = products.values().stream().toList();
        return InMemoryRespositoryUtil.applyPaginationAndSorting(allProducts, pageable, Product.class);
    }

    @Override
    public Page<Product> findByNameOrCategoryOrQuantityInStock(Pageable pageable, String name, List<String> categories, AvailabilityStatus availability) {
        List<String> loweredCaseCategories = (categories == null) ?
                Collections.emptyList() :
                categories.stream().map(String::toLowerCase).toList();

        List<Product> filteredProducts = products
                .values()
                .stream()
                .filter((product -> {
                    boolean containsName = name == null || product.getName().toLowerCase().contains(name.toLowerCase());

                    boolean containsCategories = loweredCaseCategories.isEmpty() ||
                            loweredCaseCategories.contains(product.getCategory().toLowerCase());

                    boolean isAvailableMatch;
                    if (availability == null || availability == AvailabilityStatus.ALL) {
                        isAvailableMatch = true;
                    } else if (availability == AvailabilityStatus.IN_STOCK) {
                        isAvailableMatch = product.getQuantityInStock() > 0;
                    } else { // AvailabilityStatus.OUT_OF_STOCK
                        isAvailableMatch = product.getQuantityInStock() <= 0;
                    }

                    return containsName && containsCategories && isAvailableMatch;
                }))
                .toList();
        return InMemoryRespositoryUtil.applyPaginationAndSorting(filteredProducts, pageable, Product.class);
    }
}
