package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author john
 * @since 10/07/2024
 */
public interface BeerRepository extends JpaRepository<Beer, UUID>, BeerRepositoryCustom {
    List<Beer> findByBeerNameLikeIgnoreCase(String name);
    List<Beer> findByBeerStyle(BeerStyle beerStyle);
    List<Beer> findByBeerNameLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle);
}

