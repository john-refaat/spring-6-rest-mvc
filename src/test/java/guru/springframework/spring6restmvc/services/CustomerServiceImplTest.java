package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author john
 * @since 12/07/2024
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    public static final UUID UUID_1 = UUID.randomUUID();
    public static final UUID UUID_2 = UUID.randomUUID();
    public static final String NAME_1 = "John";
    public static final String NAME_2 = "Jane";
    public static final String JANE_UPDATED = "Jane Updated";

    @Mock
    CustomerRepository customerRepository;

    CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, CustomerMapper.INSTANCE);
    }

    @Test
    void listCustomers() {
        // Given
        given(customerRepository.findAll()).willReturn(List.of(
                Customer.builder().id(UUID_1).name(NAME_1).build(),
                Customer.builder().id(UUID_2).name(NAME_2).build()));

        // When
        List<CustomerDTO> customers = customerService.listCustomers();

        // Then
        assertEquals(2, customers.size());
        assertEquals(UUID_1, customers.get(0).getId());
        assertEquals(UUID_2, customers.get(1).getId());

    }

    @Test
    void getCustomerById() {
        // Given
        given(customerRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(Customer.builder().id(UUID_2).name(NAME_2).build()));

        // When
        Optional<CustomerDTO> customer = customerService.getCustomerById(UUID_1);

        // Then
        assertNotNull(customer);
        assertFalse(customer.isEmpty());
        assertEquals(UUID_2, customer.get().getId());
        assertEquals(NAME_2, customer.get().getName());
    }

    @Test
    void save() {
        // Given
        CustomerDTO customerDTO = CustomerDTO.builder().id(UUID_2).name(NAME_2).build();
        given(customerRepository.save(any(Customer.class)))
                .willReturn(Customer.builder().id(UUID_2).name(NAME_2).build());

        // When
        CustomerDTO savedCustomerDTO = customerService.save(customerDTO);

        // Then
        assertEquals(UUID_2, savedCustomerDTO.getId());
        assertEquals(NAME_2, savedCustomerDTO.getName());
    }

    @Test
    void update() {
        // Given
        CustomerDTO updatedCustomerDTO = CustomerDTO.builder().id(UUID_2).name(JANE_UPDATED).build();
        Customer updatedCustomer = Customer.builder().id(UUID_2).name(JANE_UPDATED).build();
        given(customerRepository.findById(any(UUID.class)))
               .willReturn(Optional.of(Customer.builder().id(UUID_2).name(NAME_2).build()));
        given(customerRepository.save(any(Customer.class))).willReturn(updatedCustomer);

        // When
        CustomerDTO savedCustomerDTO = customerService.update(UUID_2, updatedCustomerDTO).get();

        // Then
        assertEquals(UUID_2, savedCustomerDTO.getId());
        assertEquals(JANE_UPDATED, savedCustomerDTO.getName());
    }

    @Test
    void deleteById() {
        // Given
        BDDMockito.given(customerRepository.existsById(any(UUID.class))).willReturn(true);

        // When
        Boolean deleted = customerService.deleteById(UUID_2);

        // Then
        assertTrue(deleted);
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(customerRepository, times(1)).deleteById(captor.capture());
        assertEquals(UUID_2, captor.getValue());
    }

    @Test
    void patchById() {
        // Given
        CustomerDTO updatedCustomerDTO = CustomerDTO.builder().id(UUID_2).name(JANE_UPDATED).build();
        Customer updatedCustomer = Customer.builder().id(UUID_2).name(JANE_UPDATED).build();
        given(customerRepository.findById(any(UUID.class)))
               .willReturn(Optional.of(Customer.builder().id(UUID_2).name(NAME_2).build()));
        given(customerRepository.save(any(Customer.class))).willReturn(updatedCustomer);

        // When
        customerService.patchById(UUID_2, updatedCustomerDTO);

        // Then
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(1)).save(captor.capture());
        assertEquals(UUID_2, captor.getValue().getId());
        assertEquals(JANE_UPDATED, captor.getValue().getName());

    }
}