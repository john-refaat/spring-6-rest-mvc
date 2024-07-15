package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author john
 * @since 02/07/2024
 */
public interface CustomerService {
    List<CustomerDTO> listCustomers();
    Optional<CustomerDTO> getCustomerById(UUID customerID);
    CustomerDTO save(CustomerDTO customer);
    Optional<CustomerDTO> update(UUID customerId, CustomerDTO customer);

    Boolean deleteById(UUID customerId);

    Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer);

    long count();
}
