package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author john
 * @since 01/07/2024
 */
public interface BeerService {
    
    List<BeerDTO> listBeers(Optional<String> beerName, Optional<BeerStyle> beerStyle);
    Optional<BeerDTO> getById(UUID id);
    BeerDTO save(BeerDTO beer);

    Optional<BeerDTO> update(UUID beerId, BeerDTO beer);

    Boolean deleteById(UUID beerId);

    void patchById(UUID beerId, BeerDTO beer);

    long count();
}
