package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * @author john
 * @since 02/08/2024
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Entity
public class BeerOrder {

    public BeerOrder(UUID id, Integer version, LocalDateTime createdDate, LocalDateTime lastModifiedDate, Customer customer, Set<BeerOrderLine> orderLines) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.setCustomer(customer);
        this.orderLines = orderLines;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(columnDefinition ="SMALLINT")
    private Integer version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.PERSIST)
    private Set<BeerOrderLine> orderLines;

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getBeerOrders().add(this);
    }
}
