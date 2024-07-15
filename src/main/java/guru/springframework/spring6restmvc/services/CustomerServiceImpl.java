package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author john
 * @since 02/07/2024
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        log.debug("List all customers - in service");
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID customerID) {
        log.debug("Get Customer by Id - in service. Id: {}", customerID.toString());
        return customerRepository.findById(customerID)
                .map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public CustomerDTO save(CustomerDTO customer) {
        log.debug("Save Customer - in service");
        return customerMapper.customerToCustomerDTO(customerRepository.save(
                customerMapper.customerDTOToCustomer(customer)));
    }

    @Override
    public Optional<CustomerDTO> update(UUID customerId, CustomerDTO customer) {
        log.debug("Update Customer - in service. Id: {}", customerId);
        AtomicReference<Optional<CustomerDTO>> reference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse(existingCustomer -> {
            existingCustomer.setName(customer.getName());
            reference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepository.save(existingCustomer))));
        },
                () -> reference.set(Optional.empty()));
        return reference.get();
    }

    @Override
    public Boolean deleteById(UUID customerId) {
        log.debug("Delete Customer - in service. Id: {}", customerId.toString());
        if(customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer) {
        log.debug("Patch Customer - in service. Id: {}", customerId.toString());
        AtomicReference<Optional<CustomerDTO>> reference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse( existingCustomer -> {
            if (customer.getName() != null) {
                existingCustomer.setName(customer.getName());
            }
            reference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepository.save(existingCustomer))));
        }, () -> reference.set(Optional.empty()));
        return reference.get();

    }

    @Override
    public long count() {
        return customerRepository.count();
    }

}
