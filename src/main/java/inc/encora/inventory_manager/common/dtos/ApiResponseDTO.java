package inc.encora.inventory_manager.common.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Standard API response wrapper")
public class ApiResponseDTO<T, K> {
    @Schema(description = "Response status", example = "Ok")
    private String status;

    @Schema(description = "HTTP status code", example = "200")
    private int statusCode;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Error information if any")
    private K error;
}
