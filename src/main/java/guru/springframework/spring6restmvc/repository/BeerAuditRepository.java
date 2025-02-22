package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.BeerAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Author:john
 * Date:18/02/2025
 * Time:05:02
 */
public interface BeerAuditRepository extends JpaRepository<BeerAudit, UUID> {
}
