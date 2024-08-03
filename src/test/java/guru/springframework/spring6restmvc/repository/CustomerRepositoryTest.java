package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.Customer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author john
 * @since 10/07/2024
 */
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    @Rollback
    @Transactional
    @Test
    void saveCustomer() {
        Customer customer = Customer.builder().name("Marina").build();
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
    }

    @Rollback
    @Transactional
    @Test
    void findCustomerById() {
        Customer customer = customerRepository.findAll().get(0);
        assertNotNull(customer);
        assertNotNull(customer.getId());
        Customer foundCustomer = customerRepository.findById(customer.getId()).orElse(null);
        assertNotNull(foundCustomer);
    }

}