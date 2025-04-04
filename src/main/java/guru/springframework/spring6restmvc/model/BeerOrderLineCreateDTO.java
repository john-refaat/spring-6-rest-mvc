package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Author:john
 * Date:03/03/2025
 * Time:05:41
 */
@Data
@Builder
public class BeerOrderLineCreateDTO {

    @NotNull(message = "Beer is required")
    private UUID beerRef;

    @NotNull(message = "Order Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer orderQuantity;
}
