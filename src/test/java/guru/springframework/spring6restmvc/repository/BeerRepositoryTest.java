package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.Category;
import guru.springframework.spring6restmvcapi.model.BeerSearchCriteria;
import guru.springframework.spring6restmvcapi.enums.BeerStyle;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 10/07/2024
 */
@Slf4j
@SpringBootTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void listBeer() {
        List<Beer> beers = beerRepository.findAll();
        Assertions.assertThat(beers).hasSizeGreaterThan(0);
    }

    @Test
    void findBeerByNameLike() throws Exception {
        Page<Beer> beers = beerRepository.findByBeerNameLikeIgnoreCase("%rise%", null);
        Assertions.assertThat(beers.getContent().size()).isEqualTo(7);
    }

    @Test
    void findBeerBySearchCriteria() throws Exception {
        List<Beer> beers = beerRepository.findBySearchCriteria(BeerSearchCriteria.builder().name("rise")
                .priceMin(BigDecimal.valueOf(11)).priceMax(BigDecimal.valueOf(12)).build());

        Assertions.assertThat(beers).isNotNull();
        Assertions.assertThat(beers).hasSizeGreaterThan(0);
        Assertions.assertThat(beers).allMatch(beer -> beer.getBeerName().toLowerCase().contains("rise"));
        Assertions.assertThat(beers).allMatch(beer -> beer.getPrice().compareTo(BigDecimal.valueOf(11)) >= 0);
        Assertions.assertThat(beers).allMatch(beer -> beer.getPrice().compareTo(BigDecimal.valueOf(12)) <= 0);
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

    @Rollback
    @Transactional
    @Test
    void addCategoryToBeer() {
        Beer beer = beerRepository.findAll().getFirst();
        Assertions.assertThat(beer.getCategories()).hasSize(0);
        Category category = Category.builder().description("Ancient Egyptian").build();
        Category savedCategory = categoryRepository.save(category);
        Assertions.assertThat(savedCategory.getId()).isNotNull();

        beer.addCategory(savedCategory);
        Beer savedBeer = beerRepository.save(beer);
        Assertions.assertThat(savedBeer.getCategories()).hasSize(1);
        Assertions.assertThat(savedBeer.getCategories().stream().findAny().get()).isEqualTo(savedCategory);
    }

    @Commit
    @Transactional
    @Test
    void testDBLock() throws InterruptedException {
        //Run in conjunction with another session on MySQL Workbench or a similar software.
        //The other session which holds a lock on the record (you should disable autocommit).
        Beer beer = beerRepository.findById(UUID.fromString("001a2f6f-b7d1-4cc3-ba3e-bf48233bdc69")).orElseThrow();
        //Hazed & Infused (2010)
        log.info("Beer Name: {}", beer.getBeerName());
        //Thread.sleep(5000);
        beer.setBeerName("Hazed & Infused (2010)4");
        beerRepository.save(beer);
        // We can make the test @Rollback,but in this case we must flush.
        //beerRepository.saveAndFlush(beer);
        log.info("Updated Beer");
    }
}