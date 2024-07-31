package guru.springframework.spring6restmvc.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * @author john
 * @since 29/07/2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BeerSearchCriteria {
    private String name;
    private BeerStyle style;
    private BigDecimal priceMin;
    private BigDecimal priceMax;
}
