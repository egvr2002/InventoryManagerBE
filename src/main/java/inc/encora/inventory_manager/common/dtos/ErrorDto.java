package inc.encora.inventory_manager.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto<T> implements Serializable {
    private String error;
    private int statusCode;
    private T message;
}
