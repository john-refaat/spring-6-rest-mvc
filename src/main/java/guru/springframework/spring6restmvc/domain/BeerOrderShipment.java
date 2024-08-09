package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author john
 * @since 05/08/2024
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "beerOrder")
@EqualsAndHashCode(exclude = "beerOrder")
@Builder
@Entity
public class BeerOrderShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(mappedBy = "beerOrderShipment")
    private BeerOrder beerOrder;

    private String trackingNumber;

    @Version
    @Column(columnDefinition = "BIGINT")
    private Integer version;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
}
