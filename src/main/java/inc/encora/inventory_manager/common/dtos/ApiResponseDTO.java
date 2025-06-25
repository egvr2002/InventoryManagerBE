package inc.encora.inventory_manager.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseDTO<T, K> {
    private String status;
    private int statusCode;
    private String message;
    private T data;
    private K error;
}
