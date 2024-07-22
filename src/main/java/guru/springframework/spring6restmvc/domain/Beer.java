package guru.springframework.spring6restmvc.domain;

import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author john
 * @since 10/07/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
public class Beer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(columnDefinition = "SMALLINT")
    private Integer version;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String beerName;

    @NotNull
    private BeerStyle beerStyle;

    @NotBlank
    private String upc;
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;

    @CreationTimestamp
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updateDate;
}
