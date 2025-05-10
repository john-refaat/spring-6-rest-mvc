package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;

import java.util.UUID;

/**
 * Author:john
 * Date:08/05/2025
 * Time:02:35
 */
public interface BeerOrderLineService {

    BeerOrderLineDTO findById(UUID id);
    BeerOrderLineDTO save(BeerOrderLineDTO beerOrderLineDTO);
    BeerOrderLineDTO update(UUID id, BeerOrderLineDTO beerOrderLineDTO);
    void delete(UUID id);
}
