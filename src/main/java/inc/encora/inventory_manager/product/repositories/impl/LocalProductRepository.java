package inc.encora.inventory_manager.product.repositories.impl;

import inc.encora.inventory_manager.common.seed.ProductSeed;
import inc.encora.inventory_manager.common.utils.InMemoryComparatorUtil;
import inc.encora.inventory_manager.product.models.Product;
import inc.encora.inventory_manager.product.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LocalProductRepository implements ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

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
        List<Product> allProducts = new ArrayList<>(products.values());
        Comparator<Product> comparator = null;
        Sort sort = pageable.getSort();

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            Comparator<Product> propertyComparator = InMemoryComparatorUtil.getPropertyComparator(Product.class, property);

            if (order.isDescending()) {
                propertyComparator = propertyComparator.reversed();
            }

            if (comparator == null) {
                comparator = propertyComparator;
            } else {
                comparator = comparator.thenComparing(propertyComparator);
            }
        }

        if (comparator != null) {
            allProducts = allProducts.stream().sorted(comparator).toList();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allProducts.size());

        if (start >= allProducts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, allProducts.size());
        }

        return new PageImpl<>(allProducts.subList(start, end), pageable, allProducts.size());
    }
}
