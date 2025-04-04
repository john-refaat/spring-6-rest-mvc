package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author john
 * @since 05/08/2024
 */
@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = "beerOrder")
@EqualsAndHashCode(exclude = "beerOrder")
@Builder
@Entity
public class BeerOrderShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "beer_order_id", referencedColumnName = "id", nullable = false)
    private BeerOrder beerOrder;

    @NotBlank
    private String trackingNumber;

    @Version
    @Column(columnDefinition = "BIGINT")
    private Integer version;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    public BeerOrderShipment(UUID id, BeerOrder beerOrder, String trackingNumber, Integer version, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.beerOrder= beerOrder;
        this.trackingNumber = trackingNumber;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    /*public void setBeerOrder(BeerOrder beerOrder) {
        this.beerOrder = beerOrder;
        beerOrder.setBeerOrderShipment(this);
    }*/
}
