package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.mappers.BeerOrderLineMapper;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import guru.springframework.spring6restmvcapi.enums.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author:john
 * Date:08/05/2025
 * Time:04:08
 */
@SpringBootTest
class DrinkPreparedListenerIT {

    @Autowired
    DrinkPreparedListener drinkPreparedListener;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerOrderLineRepository beerOrderLineRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderLineMapper beerOrderLineMapper;

    private BeerOrder savedOrder;
    private BeerOrderLineDTO beerOrderLineDTO;


    @BeforeEach
    void setUp() {
        Optional<Beer> first = beerRepository.findAll().stream().findFirst();
        Optional<Customer> customer = customerRepository.findAll().stream().findFirst();
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer.get())
                .orderLines(Set.of(BeerOrderLine.builder()
                        .beer(first.get())
                        .status(BeerOrderLineStatus.NEW)
                        .orderQuantity(1)
                        .build()))
                .build();
        savedOrder = beerOrderRepository.save(beerOrder);
        beerOrderLineRepository.flush();
        beerOrderLineDTO = beerOrderLineMapper.beerOrderLineToBeerOrderLineDTO(savedOrder.getOrderLines().iterator().next());
    }

    @Transactional
    @Test
    void drinkPrepared() {
        drinkPreparedListener.drinkPrepared(DrinkPreparedEvent.builder()
                .beerOrderLine(beerOrderLineDTO)
                .build());
        Optional<BeerOrderLine> updatedOrderLine = beerOrderLineRepository.findById(beerOrderLineDTO.getId());
        assertTrue(updatedOrderLine.isPresent());
        assertEquals(BeerOrderLineStatus.COMPLETE, updatedOrderLine.get().getStatus());
    }
}