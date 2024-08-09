package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 03/08/2024
 */
@SpringBootTest
class BeerOrderRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    private Beer beer;
    private Customer customer;

    @BeforeEach
    void setUp() {
        beer = beerRepository.findAll().getFirst();
        customer = customerRepository.findAll().getFirst();
    }

    @Rollback
    @Transactional
    @Test
    void createBeerOrder() {

        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer).orderLines(Set.of(BeerOrderLine.builder().beer(beer).orderQuantity(2).build()))
                .build();

        BeerOrder saved = beerOrderRepository.save(beerOrder);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getOrderLines().size());
        assertEquals(beer, saved.getOrderLines().iterator().next().getBeer());
        assertEquals(2, saved.getOrderLines().iterator().next().getOrderQuantity());
        assertEquals(customer, saved.getCustomer());
        assertEquals(1, beerOrderRepository.count());

        Customer savedCustomer = customerRepository.findById(customer.getId()).get();
        assertEquals(1, savedCustomer.getBeerOrders().size());
        BeerOrder order = savedCustomer.getBeerOrders().iterator().next();
        assertNotNull(order);
        BeerOrderLine orderLine = order.getOrderLines().iterator().next();
        assertNotNull(orderLine);
        assertNotNull(orderLine.getId());
    }

    @Rollback
    @Transactional
    @Test
    void createBeerOrderWithShipment() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer).orderLines(Set.of(BeerOrderLine.builder().beer(beer).orderQuantity(2).build()))
               .beerOrderShipment(BeerOrderShipment.builder().trackingNumber("rnc3456789").build())
               .build();

        BeerOrder saved = beerOrderRepository.save(beerOrder);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getOrderLines().size());
        assertEquals(beer, saved.getOrderLines().iterator().next().getBeer());
        assertEquals(2, saved.getOrderLines().iterator().next().getOrderQuantity());
        assertEquals(customer, saved.getCustomer());
        assertNotNull(saved.getBeerOrderShipment());
        assertNotNull(saved.getBeerOrderShipment().getId());
        assertEquals("rnc3456789", saved.getBeerOrderShipment().getTrackingNumber());

    }


}