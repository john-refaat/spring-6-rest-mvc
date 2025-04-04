package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:10
 */
@Data
@Builder
public class BeerOrderDTO {

    private UUID id;

    private Integer version;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @NotNull(message = "An order must be related to a customer")
    private CustomerDTO customer;

    private Set<BeerOrderLineDTO> orderLines;

    private BeerOrderShipmentDTO beerOrderShipment;
}
