package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.services.BeerOrderLineService;
import guru.springframework.spring6restmvcapi.enums.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.enums.BeerStyle;
import guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import guru.springframework.spring6restmvcapi.model.BeerDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Author:john
 * Date:08/05/2025
 * Time:03:52
 */
@ExtendWith(MockitoExtension.class)
class DrinkPreparedListenerTest {

    private static final String TEST_BEER = "Test Beer";
    private static final BigDecimal PRICE = new BigDecimal("10.99");
    private static final UUID id = UUID.randomUUID();
    private static final UUID beerId = UUID.randomUUID();
    @Mock
    BeerOrderLineService mockBeerOrderLineService;

    @InjectMocks
    DrinkPreparedListener listener;

    BeerOrderLineDTO beerOrderLineDTO;

    DrinkPreparedEvent event;

    @BeforeEach
    void setUp() {
        beerOrderLineDTO = BeerOrderLineDTO.builder().id(id)
                .beer(BeerDTO.builder().id(beerId).beerName("Test Beer").beerStyle(BeerStyle.ALE).upc("123456789012")
                        .quantityOnHand(100).price(PRICE).build())
                .orderQuantity(5)
                .status(BeerOrderLineStatus.NEW).build();
        event = new DrinkPreparedEvent(beerOrderLineDTO);
    }

    // Verify that when a valid DrinkPreparedEvent is received, the BeerOrderLine status is updated to COMPLETE
    @Test
    public void test_valid_drink_prepared_event_updates_status_to_complete() {
        // Given
        BDDMockito.given(mockBeerOrderLineService.update(any(UUID.class), any(BeerOrderLineDTO.class))).willReturn(beerOrderLineDTO);
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<BeerOrderLineDTO> beerOrderLineDTOCaptor = ArgumentCaptor.forClass(BeerOrderLineDTO.class);
        assertEquals(BeerOrderLineStatus.NEW, beerOrderLineDTO.getStatus());

        // When
        listener.drinkPrepared(event);

        // Then
        assertEquals(BeerOrderLineStatus.COMPLETE, beerOrderLineDTO.getStatus());
        verify(mockBeerOrderLineService).update(captor.capture(), beerOrderLineDTOCaptor.capture());
        assertEquals(beerOrderLineDTO.getId(), captor.getValue());
        assertEquals(beerOrderLineDTO.getBeer(), beerOrderLineDTOCaptor.getValue().getBeer());
        assertEquals(beerOrderLineDTO.getOrderQuantity(), beerOrderLineDTOCaptor.getValue().getOrderQuantity());
        assertEquals(BeerOrderLineStatus.COMPLETE, beerOrderLineDTOCaptor.getValue().getStatus());
    }



}