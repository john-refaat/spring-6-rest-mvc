package guru.springframework.spring6restmvc.domain;

import guru.springframework.spring6restmvcapi.enums.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name= "beer_category", joinColumns = @JoinColumn(name = "beer_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    public void addCategory(Category category) {
        categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBeers().remove(this);
    }
}
