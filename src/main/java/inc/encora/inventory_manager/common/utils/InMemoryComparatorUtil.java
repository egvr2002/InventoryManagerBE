package inc.encora.inventory_manager.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public class InMemoryComparatorUtil {
    public static <T> Comparator<T> getPropertyComparator(Class<T> entity, String property) {
        String getterName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);

        try {
            Method getter = entity.getMethod(getterName);
            return (o1, o2) -> {
                try {
                    Object value1 = getter.invoke(o1);
                    Object value2 = getter.invoke(o2);

                    // Handle null values with nullsLast (or nullsFirst)
                    if (value1 == null && value2 == null) return 0;
                    if (value1 == null) return 1; // value1 is null, value2 is not, so value1 comes last
                    if (value2 == null) return -1; // value2 is null, value1 is not, so value2 comes last

                    if (value1 instanceof Comparable) {
                        @SuppressWarnings("unchecked")
                        Comparable<Object> comparableValue1 = (Comparable<Object>) value1;
                        return comparableValue1.compareTo(value2);
                    } else {
                        // The value is not comparable, so we treat it as equal
                        return 0;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Error accessing sort property");
                }
            };
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Unsupported sort property");
        }
    }
}
