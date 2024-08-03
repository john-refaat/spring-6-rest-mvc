package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author john
 * @since 03/08/2024
 */
public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {
    
}
