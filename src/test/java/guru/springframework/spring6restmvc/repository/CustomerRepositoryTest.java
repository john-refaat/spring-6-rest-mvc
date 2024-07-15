package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Customer;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 10/07/2024
 */
@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void saveCustomer() {
        Customer customer = Customer.builder().name("John").build();
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer);
        assertNotNull(savedCustomer.getId());
    }
}