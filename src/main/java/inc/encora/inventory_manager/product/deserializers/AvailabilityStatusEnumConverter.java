package inc.encora.inventory_manager.product.deserializers;

import inc.encora.inventory_manager.product.constants.AvailabilityStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityStatusEnumConverter implements Converter<String, AvailabilityStatus> {
    @Override
    public AvailabilityStatus convert(String source) {
        return AvailabilityStatus.fromValue(source);
    }
}
