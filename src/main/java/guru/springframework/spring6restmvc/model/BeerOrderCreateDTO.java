package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

/**
 * Author:john
 * Date:03/03/2025
 * Time:04:44
 */
@Data
@Builder
public class BeerOrderCreateDTO {

    @NotNull(message = "An order must be related to a customer")
    private UUID customerId;

    private Set<BeerOrderLineCreateDTO> orderLines;

    private BeerOrderShipmentDTO beerOrderShipment;
}
