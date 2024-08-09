package guru.springframework.spring6restmvc.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author john
 * @since 03/08/2024
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "beers")
@EqualsAndHashCode(exclude = "beers")
@Builder
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 100, columnDefinition = "varchar(100)", nullable = false, updatable = false)
    private UUID id;

    @Version
    @Column(columnDefinition = "SMALLINT")
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;

    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "categories")
    private Set<Beer> beers = new HashSet<>();
}
