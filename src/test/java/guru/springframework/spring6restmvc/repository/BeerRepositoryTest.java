package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 10/07/2024
 */
@SpringBootTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void listBeer() {
        List<Beer> beers = beerRepository.findAll();
        Assertions.assertThat(beers).hasSizeGreaterThan(0);
    }

    @Test
    void findBeerByNameLike() throws Exception {
        List<Beer> beers = beerRepository.findByBeerNameLikeIgnoreCase("%rise%");
        Assertions.assertThat(beers.size()).isEqualTo(7);
    }

    @Rollback
    @Transactional
    @Test
    void saveBeer() {
        Beer beer = Beer.builder().beerName("Test Beer").upc("876543").beerStyle(BeerStyle.WHEAT).price(BigDecimal.TEN).build();
        Beer savedBeer = beerRepository.save(beer);
        Assertions.assertThat(savedBeer).isNotNull();
        Assertions.assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void saveBeerWithoutName() {
        Beer beer = Beer.builder().upc("876543").beerStyle(BeerStyle.WHEAT).price(BigDecimal.TEN).build();
        assertThrows(TransactionSystemException.class, () -> {
            beerRepository.save(beer);
            beerRepository.flush();
        });
    }

    @Test
    void saveBeerNameTooLong() {
     Beer beer = Beer.builder().beerName("This is a very long beer name that should not be saved")
         .upc("876543").beerStyle(BeerStyle.WHEAT).price(BigDecimal.TEN).build();
     assertThrows(TransactionSystemException.class, () -> {
         beerRepository.save(beer);
         beerRepository.flush();    
     });

    }
}