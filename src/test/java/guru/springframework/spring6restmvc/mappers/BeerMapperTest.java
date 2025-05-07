package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvcapi.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 10/07/2024
 */
class BeerMapperTest {

    public static final String MY_BEER = "My Beer";
    public static final BigDecimal PRICE = BigDecimal.valueOf(12.5);
    BeerMapper beerMapper;

    @BeforeEach
    void setUp() {
        beerMapper = BeerMapper.INSTANCE;
    }

    @Test
    void beertDTOtoBeer() {
        // given
       BeerDTO beerDTO = BeerDTO.builder().beerName(MY_BEER).price(PRICE).build();

        // when
        Beer beer = beerMapper.beerDTOToBeer(beerDTO);

        // then
        assertEquals(MY_BEER, beer.getBeerName());
        assertEquals(PRICE, beer.getPrice());
    }

    @Test
    void beerToBeerDTO() {
        // given
        Beer beer = Beer.builder().beerName(MY_BEER).price(PRICE).build();

        // when
        BeerDTO beerDTO = beerMapper.beerToBeerDTO(beer);

        // then
        assertEquals(MY_BEER, beerDTO.getBeerName());
        assertEquals(PRICE, beerDTO.getPrice());
    }
}