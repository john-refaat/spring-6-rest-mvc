package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerSearchCriteria;

import java.util.List;

/**
 * @author john
 * @since 29/07/2024
 */
public interface BeerRepositoryCustom {
    List<Beer> findBySearchCriteria(BeerSearchCriteria criteria);
}
