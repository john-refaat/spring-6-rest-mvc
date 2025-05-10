package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderLineMapper;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvcapi.enums.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.enums.BeerStyle;
import guru.springframework.spring6restmvcapi.model.BeerDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Author:john
 * Date:08/05/2025
 * Time:03:27
 */
@ExtendWith(MockitoExtension.class)
class BeerOrderLineServiceImplTest {

    private static final String TEST_BEER = "Test Beer";
    private static final BigDecimal PRICE = new BigDecimal("10.99");
    private static final UUID id = UUID.randomUUID();
    private static final UUID beerId = UUID.randomUUID();
    @Mock
    BeerOrderLineRepository beerOrderLineRepository;

    BeerOrderLineMapper beerOrderLineMapper;
    BeerMapper beerMapper;

    BeerOrderLineService beerOrderLineService;

    BeerOrderLineDTO beerOrderLineDTO;
    BeerOrderLine beerOrderLine;


    @BeforeEach
    void setUp() {
        beerOrderLineMapper = BeerOrderLineMapper.INSTANCE;
        beerMapper = BeerMapper.INSTANCE;
        beerOrderLineService = new BeerOrderLineServiceImpl(beerOrderLineRepository, beerOrderLineMapper, beerMapper);

        beerOrderLine = BeerOrderLine.builder().id(id)
                .beer(Beer.builder().id(beerId).beerName(TEST_BEER).beerStyle(BeerStyle.ALE).upc("123456789012")
                        .quantityOnHand(100).price(PRICE).build())
        .orderQuantity(5)
        .status(BeerOrderLineStatus.NEW).build();
        beerOrderLineDTO = BeerOrderLineDTO.builder().id(id)
                .beer(BeerDTO.builder().id(beerId).beerName("Test Beer").beerStyle(BeerStyle.ALE).upc("123456789012")
                        .quantityOnHand(100).price(PRICE).build())
                .orderQuantity(5)
                .status(BeerOrderLineStatus.NEW).build();

    }

    @Test
    void findById() {
        // Given
        BDDMockito.given(beerOrderLineRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(beerOrderLine));
        // When
        BeerOrderLineDTO result = beerOrderLineService.findById(UUID.randomUUID());
        // Then
        assertEquals(beerOrderLineDTO, result);
    }

    @Test
    void save() {
        // Given
        beerOrderLineDTO.setId(null);
        BDDMockito.given(beerOrderLineRepository.save(any(BeerOrderLine.class)))
                .willReturn(beerOrderLine);
        // When
        BeerOrderLineDTO result = beerOrderLineService.save(beerOrderLineDTO);
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(beerOrderLineDTO.getBeer(), result.getBeer());
        assertEquals(beerOrderLineDTO.getOrderQuantity(), result.getOrderQuantity());
        assertEquals(beerOrderLineDTO.getStatus(), result.getStatus());
    }

    @Test
    void update() {
        // Given
        beerOrderLineDTO.setStatus(BeerOrderLineStatus.COMPLETE);
        BDDMockito.given(beerOrderLineRepository.findById(any(UUID.class))).willReturn(Optional.of(beerOrderLine));
        BDDMockito.given(beerOrderLineRepository.save(any(BeerOrderLine.class))).willReturn(beerOrderLine);
        // When
        BeerOrderLineDTO result = beerOrderLineService.update(UUID.randomUUID(), beerOrderLineDTO);
        // Then
        assertEquals(beerOrderLineDTO, result);
    }

    @Test
    void delete() {
        // Given
        BDDMockito.given(beerOrderLineRepository.existsById(any(UUID.class))).willReturn(true);
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);

        // When

        UUID id1 = UUID.randomUUID();
        beerOrderLineService.delete(id1);
        // Then
        verify(beerOrderLineRepository).deleteById(captor.capture());
        assertEquals(id1, captor.getValue());

    }
}