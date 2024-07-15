package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author john
 * @since 10/07/2024
 */
public interface BeerRepository extends JpaRepository<Beer, UUID> {
}
