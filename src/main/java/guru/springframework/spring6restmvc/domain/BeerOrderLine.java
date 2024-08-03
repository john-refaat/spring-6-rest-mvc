package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.SmallIntJdbcType;

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

    @ManyToOne
    private Beer beer;

    private Integer orderQuantity;

    private Integer quantityAllocated;

    @Version
    @Column(columnDefinition = "SMALLINT")
    private Integer version;

    @ManyToOne
    @JoinColumn(name = "beer_order_id", nullable = false)
    private BeerOrder beerOrder;


}