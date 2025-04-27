package guru.springframework.spring6restmvc.model;

import guru.springframework.spring6restmvc.enums.BeerOrderLineStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:11
 */
@Data
@Builder
public class BeerOrderLineDTO {
    private UUID id;

    @NotNull(message = "Beer is required")
    private BeerDTO beer;

    @NotNull(message = "Order Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer orderQuantity;

    private Integer quantityAllocated;

    private BeerOrderLineStatus status;

    private Integer version;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
