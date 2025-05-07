package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import guru.springframework.spring6restmvcapi.model.CustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author john
 * @since 02/07/2024
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = "customerListCache")
    @Override
    public List<CustomerDTO> listCustomers() {
        log.debug("List all customers - in service");
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "customerCache")
    @Override
    public Optional<CustomerDTO> getCustomerById(UUID customerID) {
        log.debug("Get Customer by Id - in service. Id: {}", customerID.toString());
        return customerRepository.findById(customerID)
                .map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public CustomerDTO save(CustomerDTO customer) {
        log.debug("Save Customer - in service");
        clearCustomerListCache();
        return customerMapper.customerToCustomerDTO(customerRepository.save(
                customerMapper.customerDTOToCustomer(customer)));
    }

    @Override
    public Optional<CustomerDTO> update(UUID customerId, CustomerDTO customer) {
        log.debug("Update Customer - in service. Id: {}", customerId);
        evictCustomerCache(customerId);
        clearCustomerListCache();
        AtomicReference<Optional<CustomerDTO>> reference = new AtomicReference<>();
        customerRepository.findById(customerId).ifPresentOrElse(existingCustomer -> {
            existingCustomer.setName(customer.getName());
            reference.set(Optional.of(customerMapper.customerToCustomerDTO(customerRepository.save(existingCustomer))));
        },
                () -> reference.set(Optional.empty()));
        return reference.get();
    }

    private void clearCustomerListCache() {
        log.info("Evict customer list cache");
        if (cacheManager.getCache("customerListCache") != null)
            cacheManager.getCache("customerListCache").clear();
    }

    private void evictCustomerCache(UUID customerId) {
        log.info("Evict customer cache for id: {}", customerId.toString());
        if (cacheManager.getCache("customerCache") != null)
            cacheManager.getCache("customerCache").evict(customerId);
    }

    @Override
    public Boolean deleteById(UUID customerId) {
        log.debug("Delete Customer - in service. Id: {}", customerId.toString());
        evictCustomerCache(customerId);
        clearCustomerListCache();
        if(customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patchById(UUID customerId, CustomerDTO customer) {
        log.debug("Patch Customer - in service. Id: {}", customerId.toString());
        evictCustomerCache(customerId);
        clearCustomerListCache();
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
