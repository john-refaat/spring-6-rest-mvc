package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.TransactionSystemException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 12/07/2024
 */
@SpringBootTest
class BeerControllerIT {

    public static final String RISE = "rise";
    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @BeforeEach
    void setUp() {
        System.out.println("# Beers Saved: "+beerRepository.count());
    }

    @Test
    void listBeers() {
        List<BeerDTO> beerDTOS = beerController.listBeers(null, null);
        assertNotNull(beerDTOS);
        assertEquals(beerRepository.count(), beerDTOS.size());
        assertNotNull(beerDTOS.getFirst());
        assertNotNull(beerDTOS.getLast());
    }

    @Test
    void listBeersByBeerName() {
        List<BeerDTO> beerDTOS = beerController.listBeers(RISE, null);
        assertNotNull(beerDTOS);
        assertEquals(7, beerDTOS.size());
        Assertions.assertThat(beerDTOS.getFirst().getBeerName()).containsIgnoringCase(RISE);
        Assertions.assertThat(beerDTOS.getLast().getBeerName()).containsIgnoringCase(RISE);
    }

    @Test
    void getById() {
        UUID beerId = beerRepository.findAll().getFirst().getId();
        BeerDTO beerDTO = beerController.getById(beerId);
        assertNotNull(beerDTO);
        assertEquals(beerDTO.getId(), beerId);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getById(UUID.randomUUID()); // should throw exception as beerId is null
        });
    }

    @Rollback
    @Transactional
    @Test
    void save() {
        BeerDTO beerDTO = BeerDTO.builder().beerName("Test Beer")
                .beerStyle(BeerStyle.IPA).upc("1234567890123")
                .quantityOnHand(100)
                .build();
        HttpServletResponse response = new MockHttpServletResponse();
        BeerDTO savedBeerDTO = beerController.save(beerDTO, response);
        assertNotNull(savedBeerDTO);
        assertNotNull(savedBeerDTO.getId());
        assertEquals(beerDTO.getBeerName(), savedBeerDTO.getBeerName());
        assertEquals(beerDTO.getBeerStyle(), savedBeerDTO.getBeerStyle());
        assertEquals(beerDTO.getUpc(), savedBeerDTO.getUpc());
        assertEquals(BeerController.PATH + "/" + savedBeerDTO.getId().toString(), response.getHeader("Location"));
        assertNotNull(beerRepository.findById(savedBeerDTO.getId()));
    }

    @Test
    void saveBeerNameTooLong() {
        BeerDTO beerDTO = BeerDTO.builder().beerName("Test Beer with a very long name that should not be saved to the database")
                .beerStyle(BeerStyle.IPA).upc("1234567890123").price(BigDecimal.TEN)
                .quantityOnHand(100)
                .build();
        assertThrows(TransactionSystemException.class, () -> {
            beerController.save(beerDTO, new MockHttpServletResponse()); // should throw exception as beerName is too long
        });
    }

    @Rollback
    @Transactional
    @Test
    void update() {
        Beer beer = Beer.builder().beerName("Stella")
                .beerStyle(BeerStyle.WHEAT).upc("1234567890123")
                .quantityOnHand(100)
                .build();
        Beer savedBeer = beerRepository.save(beer);
        savedBeer.setBeerName("Stella2");
        savedBeer.setBeerStyle(BeerStyle.ALE);
        savedBeer.setQuantityOnHand(101);
        savedBeer.setPrice(BigDecimal.valueOf(12));
        BeerDTO updatedBeer = beerController.update(savedBeer.getId(), beerMapper.beerToBeerDTO(savedBeer));
        assertNotNull(updatedBeer);
        assertEquals(savedBeer.getId(), updatedBeer.getId());
        assertEquals("Stella2", updatedBeer.getBeerName());
        assertEquals(BeerStyle.ALE, updatedBeer.getBeerStyle());
        assertEquals(101, updatedBeer.getQuantityOnHand());
        assertEquals(BigDecimal.valueOf(12), updatedBeer.getPrice());

    }

    @Rollback
    @Transactional
    @Test
    void patchBeerById() {
        Beer beer = Beer.builder().beerName("Stella")
                .beerStyle(BeerStyle.WHEAT).upc("1234567890123")
                .quantityOnHand(100)
                .build();
        Beer savedBeer = beerRepository.save(beer);
        BeerDTO beerDTO = BeerDTO.builder().beerName("Stella2")
                .beerStyle(BeerStyle.ALE).upc("1234567890123")
                .quantityOnHand(101)
                .build();
        beerController.patchBeerById(savedBeer.getId(), beerDTO);
        Beer updatedBeer = beerRepository.findById(savedBeer.getId()).orElse(null);
        assertNotNull(updatedBeer);
        assertEquals("Stella2", updatedBeer.getBeerName());
        assertEquals("1234567890123", updatedBeer.getUpc());
        assertEquals(BeerStyle.ALE, updatedBeer.getBeerStyle());
        assertEquals(101, updatedBeer.getQuantityOnHand());
    }

    @Rollback
    @Transactional
    @Test
    void deleteBeer() {
        UUID beerId = beerRepository.findAll().getFirst().getId();
        long count = beerRepository.count();
        beerController.deleteCustomer(beerId);
        assertNull(beerRepository.findById(beerId).orElse(null));
        assertEquals(count - 1, beerRepository.count());
    }

    @Rollback
    @Transactional
    @Test
    void deleteBeerNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteCustomer(UUID.randomUUID()); // should throw exception as beerId is null
        });
    }

}