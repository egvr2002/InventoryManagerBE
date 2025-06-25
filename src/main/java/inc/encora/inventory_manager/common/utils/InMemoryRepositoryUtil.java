package inc.encora.inventory_manager.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InMemoryRepositoryUtil {
    public static <T> Page<T> applyPaginationAndSorting(List<T> data, Pageable pageable, Class<T> entityClass) {
        Comparator<T> comparator = null;
        Sort sort = pageable.getSort();

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            Comparator<T> propertyComparator = InMemoryComparatorUtil.getPropertyComparator(entityClass, property);

            if (order.isDescending()) {
                propertyComparator = propertyComparator.reversed();
            }

            if (comparator == null) {
                comparator = propertyComparator;
            } else {
                comparator = comparator.thenComparing(propertyComparator);
            }
        }

        List<T> sortedData = new ArrayList<>(data);
        if (comparator != null) {
            sortedData.sort(comparator);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedData.size());

        List<T> pagedData;
        if (start >= sortedData.size()) {
            pagedData = Collections.emptyList();
        } else {
            pagedData = sortedData.subList(start, end);
        }

        return new PageImpl<>(pagedData, pageable, sortedData.size());
    }
}
