package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Author:john
 * Date:08/05/2025
 * Time:02:34
 */
public interface BeerOrderLineRepository extends JpaRepository<BeerOrderLine, UUID> {

}
