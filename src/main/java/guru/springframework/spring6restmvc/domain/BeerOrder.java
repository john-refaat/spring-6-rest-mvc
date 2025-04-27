package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
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

    public BeerOrder(UUID id, Integer version, LocalDateTime createdDate, LocalDateTime lastModifiedDate, Customer customer,
                     Set<BeerOrderLine> orderLines, BeerOrderShipment beerOrderShipment, BigDecimal paymentAmount) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.setCustomer(customer);
        this.setOrderLines(orderLines);
        this.setBeerOrderShipment(beerOrderShipment);
        this.setPaymentAmount(paymentAmount);
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

    @NotNull
    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    private Set<BeerOrderLine> orderLines;

    @OneToOne(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    private BeerOrderShipment beerOrderShipment;

    private BigDecimal paymentAmount;

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            customer.getBeerOrders().add(this);
        }
    }

    public void setBeerOrderShipment(BeerOrderShipment beerOrderShipment) {
        if (beerOrderShipment != null) {
            this.beerOrderShipment = beerOrderShipment;
            beerOrderShipment.setBeerOrder(this);
        }
    }

    public void setOrderLines(Set<BeerOrderLine> orderLines) {
        if (orderLines!= null) {
            this.orderLines = orderLines;
            orderLines.forEach(orderLine -> orderLine.setBeerOrder(this));
        }
    }
}
