package guru.springframework.spring6restmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by jt, Spring Framework Guru.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
    private UUID id;
    private Integer version;

    @NotBlank(message = "Beer Name is required")
    private String beerName;

    @NotNull(message = "Beer Style is required")
    private BeerStyle beerStyle;

    @NotBlank(message = "UPC is required")
    private String upc;

    private Integer quantityOnHand;

    @NotNull(message = "Price is required")
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}