package inc.encora.inventory_manager.product.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AvailabilityStatus {
    IN_STOCK("in_stock"),
    OUT_OF_STOCK("out_of_stock"),
    ALL("all");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }


    @JsonCreator
    public static AvailabilityStatus fromValue(String text) {
        for (AvailabilityStatus status : AvailabilityStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant " + AvailabilityStatus.class.getCanonicalName() + " for value " + text);
    }
}
