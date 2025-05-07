package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.mappers.BeerOrderCreateMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvcapi.model.BeerDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import guru.springframework.spring6restmvcapi.model.CustomerDTO;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Author:john
 * Date:26/02/2025
 * Time:02:09
 */
@ExtendWith(MockitoExtension.class)
class BeerOrderServiceImplTest {

    private static final String BEER_1 = "Beer1";
    private static final String JOHN = "John";
    private static final String BEER_2 = "Beer2";
    private static final String ALICE = "Alice";
    private static final UUID ID_1 = UUID.randomUUID();
    private static final UUID ID_2 = UUID.randomUUID();
    BeerOrderServiceImpl beerOrderService;

    @Mock
    BeerOrderRepository beerOrderRepository;

    @Mock
    BeerRepository beerRepository;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    CustomerRepository customerRepository;

    BeerOrderMapper beerOrderMapper;
    BeerOrderCreateMapper beerOrderCreateMapper;

    BeerOrder beerOrder1;
    BeerOrder beerOrder2;

    BeerOrderDTO beerOrderDTO;


    @BeforeEach
    void setUp() {
        beerOrderMapper = BeerOrderMapper.INSTANCE;
        beerOrderCreateMapper = BeerOrderCreateMapper.INSTANCE;

        beerOrderService = new BeerOrderServiceImpl(beerOrderRepository, beerOrderMapper, beerOrderCreateMapper,
                beerRepository, customerRepository, applicationEventPublisher);
        beerOrder1 = BeerOrder.builder().id(ID_1)
                .orderLines(Set.of(
                        BeerOrderLine.builder().beer(
                                Beer.builder().id(UUID.randomUUID()).beerName(BEER_1).build()).build()))
                .customer(Customer.builder().name(JOHN).build())
                .build();

        beerOrder2 = BeerOrder.builder().id(ID_2)
                .orderLines(Set.of(
                        BeerOrderLine.builder().beer(
                                Beer.builder().id(UUID.randomUUID()).beerName(BEER_2).build()).build()))
                .customer(Customer.builder().name(ALICE).build())
                .build();

        beerOrderDTO = BeerOrderDTO.builder().id(ID_1)
                .orderLines(Set.of(
                        BeerOrderLineDTO.builder().beer(
                                BeerDTO.builder().id(UUID.randomUUID()).beerName(BEER_1).build()).build()))
                .customer(CustomerDTO.builder().name(JOHN).build())
                .build();

    }


    @Test
    void listOrders() {
        //given
        Page<BeerOrder> beerOrders = new PageImpl<>(List.of(beerOrder1, beerOrder2));
        given(beerOrderRepository.findAll(any(Pageable.class))).willReturn(beerOrders);

        //when
        Page<BeerOrderDTO> beerOrderDTOs = beerOrderService.listOrders(1, 10);

        //then
        assertEquals(2, beerOrderDTOs.getContent().size());
        assertEquals(JOHN, beerOrderDTOs.getContent().get(0).getCustomer().getName());
        assertEquals(BEER_1, beerOrderDTOs.getContent().get(0).getOrderLines().iterator().next().getBeer().getBeerName());
        assertEquals(ALICE, beerOrderDTOs.getContent().get(1).getCustomer().getName());
        assertEquals(BEER_2, beerOrderDTOs.getContent().get(1).getOrderLines().stream().findFirst().get().getBeer().getBeerName());
    }

    @Test
    void createOrder() {
        //given
        given(beerOrderRepository.save(any(BeerOrder.class))).willReturn(beerOrder1);

        //when
        BeerOrderDTO savedBeerOrderDTO = beerOrderService.createOrder(beerOrderDTO);

        //then
        assertEquals(beerOrderDTO.getId(), savedBeerOrderDTO.getId());
        assertEquals(JOHN, savedBeerOrderDTO.getCustomer().getName());
        assertEquals(BEER_1, savedBeerOrderDTO.getOrderLines().iterator().next().getBeer().getBeerName());
    }


    @Test
    void getOrderBuId() {
        //given
        given(beerOrderRepository.findById(any(UUID.class))).willReturn(Optional.of(beerOrder1));

        //when
        BeerOrderDTO beerOrderDTO = beerOrderService.getOrderById(ID_1).get();

        //then
        assertEquals(beerOrder1.getId(), beerOrderDTO.getId());
        assertEquals(JOHN, beerOrderDTO.getCustomer().getName());
        assertEquals(BEER_1, beerOrderDTO.getOrderLines().iterator().next().getBeer().getBeerName());
    }

    @Test
    void updateOrder() {
        //given

        given(beerOrderRepository.findById(any(UUID.class))).willReturn(Optional.of(beerOrder1));

        BeerOrder updatedBeerOrder = BeerOrder.builder().id(ID_1)
                .orderLines(Set.of(
                        BeerOrderLine.builder().beer(
                                Beer.builder().id(UUID.randomUUID()).beerName(BEER_2).build()).build()))
                .customer(Customer.builder().name(ALICE).build())
                .build();

        given(beerOrderRepository.save(any())).willReturn(updatedBeerOrder);
        ArgumentCaptor<BeerOrder> captor = ArgumentCaptor.forClass(BeerOrder.class);

        //when
        beerOrderDTO.setCustomer(CustomerDTO.builder().name(ALICE).build());
        beerOrderDTO.getOrderLines().iterator().next().getBeer().setBeerName(BEER_2);
        BeerOrderDTO updatedBeerOrderDTO = beerOrderService.updateOrder(ID_1, beerOrderDTO).get();

        //then
        verify(beerOrderRepository).save(captor.capture());
        assertEquals(beerOrder1.getId(), updatedBeerOrderDTO.getId());
        assertEquals(ALICE, updatedBeerOrderDTO.getCustomer().getName());
        assertEquals(BEER_2, updatedBeerOrderDTO.getOrderLines().stream().findFirst().get().getBeer().getBeerName());
        assertEquals(ID_1, captor.getValue().getId());
        assertEquals(1, captor.getValue().getOrderLines().size());
        assertEquals(ALICE, captor.getValue().getCustomer().getName());
        assertEquals(BEER_2, captor.getValue().getOrderLines().iterator().next().getBeer().getBeerName());
    }

    @Test
    void deleteOrder() {
        //given
        given(beerOrderRepository.existsById(any(UUID.class))).willReturn(true);
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);

        //when
        beerOrderService.deleteOrder(ID_1);

        //then
        verify(beerOrderRepository).deleteById(captor.capture());
        assertEquals(ID_1, captor.getValue());
    }

    @Test
    void patchOrder() {
        //given
        given(beerOrderRepository.findById(any(UUID.class))).willReturn(Optional.of(beerOrder1));

        BeerOrder updatedBeerOrder = BeerOrder.builder().id(ID_1)
                .orderLines(Set.of(
                        BeerOrderLine.builder().beer(
                                Beer.builder().id(UUID.randomUUID()).beerName(BEER_2).build()).build()))
                .customer(Customer.builder().name(JOHN).build())
                .build();

        given(beerOrderRepository.save(any())).willReturn(updatedBeerOrder);
        ArgumentCaptor<BeerOrder> captor = ArgumentCaptor.forClass(BeerOrder.class);

        //when
        beerOrderDTO.getOrderLines().iterator().next().getBeer().setBeerName(BEER_2);
        beerOrderService.patchOrder(ID_1, beerOrderDTO);

        //then
        verify(beerOrderRepository).save(captor.capture());
        assertEquals(beerOrder1.getId(), updatedBeerOrder.getId());
        assertEquals(JOHN, updatedBeerOrder.getCustomer().getName());
        assertEquals(BEER_2, updatedBeerOrder.getOrderLines().stream().findFirst().get().getBeer().getBeerName());
        assertEquals(ID_1, captor.getValue().getId());
        assertEquals(1, captor.getValue().getOrderLines().size());
        assertEquals(JOHN, captor.getValue().getCustomer().getName());
        assertEquals(BEER_2, captor.getValue().getOrderLines().iterator().next().getBeer().getBeerName());
    }

    @Test
    void count() {
        //given
        given(beerOrderRepository.count()).willReturn(2L);

        //when
        long count = beerOrderService.count();

        //then
        assertEquals(2L, count);
    }
}