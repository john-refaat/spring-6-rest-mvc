package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


/**
 * @author john
 * @since 10/07/2024
 */
public interface BeerRepository extends JpaRepository<Beer, UUID>, BeerRepositoryCustom {
    Page<Beer> findByBeerNameLikeIgnoreCase(String name, Pageable page);
    Page<Beer> findByBeerStyle(BeerStyle beerStyle, Pageable pageable);
    Page<Beer> findByBeerNameLikeIgnoreCaseAndBeerStyle(String beerName, BeerStyle beerStyle, Pageable pageable);

}

