package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.BeerOrderShipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author john
 * @since 21/08/2024
 */
public interface BeerOrderShipmentRepository extends JpaRepository<BeerOrderShipment, UUID> {

}
