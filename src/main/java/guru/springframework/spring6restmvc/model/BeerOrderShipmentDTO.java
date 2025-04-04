package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:20
 */
@Data
@Builder
public class BeerOrderShipmentDTO {

    private UUID id;

    @NotBlank(message = "Tracking Number is required")
    private String trackingNumber;

    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

}
