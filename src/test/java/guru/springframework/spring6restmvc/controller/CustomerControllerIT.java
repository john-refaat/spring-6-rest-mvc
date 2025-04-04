package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author john
 * @since 14/07/2024
 */
@SpringBootTest
public class CustomerControllerIT {
    public static final String BASE_PATH = "/api/v1/customer/";
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;

    @Test
    void getListOfCustomers() {
        List<CustomerDTO> customerDTOS = customerController.listCustomers();
        assertThat((long) customerDTOS.size(), equalTo(customerRepository.count()));
    }

    @Test
    void getCustomerById() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());
        assertThat(customerDTO.getId(), equalTo(customer.getId()));
        assertThat(customerDTO.getName(), equalTo(customer.getName()));
    }

    @Test
    void getCustomerByIdNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Rollback
    @Transactional
    @Test
    void createCustomer() {
        CustomerDTO newCustomer = CustomerDTO.builder().name("Donald").build();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomerDTO savedCustomer = customerController.createCustomer(newCustomer, response);
        Assertions.assertNotNull(savedCustomer.getId());
        assertThat(savedCustomer.getName(), equalTo(newCustomer.getName()));
        assertTrue(response.containsHeader("Location"));
        assertThat(response.getHeader("Location"), equalTo(BASE_PATH +savedCustomer.getId()));
    }

    @Rollback
    @Transactional
    @Test
    void updateCustomer() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDTO updatedCustomer = CustomerDTO.builder().id(customer.getId()).name("Donald Updated").build();
        customerController.updateCustomer(customer.getId(), updatedCustomer);
        CustomerDTO retrievedCustomer = customerController.getCustomerById(customer.getId());
        assertThat(retrievedCustomer.getName(), equalTo(updatedCustomer.getName()));
    }

    @Rollback
    @Transactional
    @Test
    void updateCustomerNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            customerController.updateCustomer(UUID.randomUUID(), CustomerDTO.builder().name("Donald Updated").build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void patchCustomerById() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDTO updatedCustomer = CustomerDTO.builder().id(customer.getId()).name("Donald Updated").build();
        customerController.patchCustomerById(customer.getId(), updatedCustomer);
        CustomerDTO retrievedCustomer = customerController.getCustomerById(customer.getId());
        assertThat(retrievedCustomer.getId(), equalTo(updatedCustomer.getId()));
        assertThat(retrievedCustomer.getName(), equalTo(updatedCustomer.getName()));
    }

    @Rollback
    @Transactional
    @Test
    void patchCustomerNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            customerController.patchCustomerById(UUID.randomUUID(), CustomerDTO.builder().name("Donald Updated").build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void deleteCustomer() {
        Customer customer = customerRepository.save(Customer.builder().name("Joe").build());
        long count = customerRepository.count();
        assertTrue(count > 0);
        customerController.deleteCustomer(customer.getId());
        assertThat(customerRepository.count(), equalTo(count - 1));
    }

    @Rollback
    @Transactional
    @Test
    void deleteCustomerNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            customerController.deleteCustomer(UUID.randomUUID());
        });
    }


}
