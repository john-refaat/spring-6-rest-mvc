package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
 * @since 02/08/2024
 */
@Setter
@Getter
@EqualsAndHashCode(exclude = {"beerOrder"})
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"beerOrder"})
@Builder
@Entity
public class BeerOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @ManyToOne
    private Beer beer;

    @NotNull(message = "Order Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer orderQuantity;

    private Integer quantityAllocated;

    @Version
    @Column(columnDefinition = "SMALLINT")
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "beer_order_id", nullable = false)
    private BeerOrder beerOrder;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

}