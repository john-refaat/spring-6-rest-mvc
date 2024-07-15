package guru.springframework.spring6restmvc.controller;

/**
 * @author john
 * @since 02/07/2024
 */
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    public static final String PATH = "/api/v1/customer";
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping({"", "/"})
    public List<CustomerDTO> listCustomers() {
        log.debug("List customers");
        return customerService.listCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustomerById(@PathVariable UUID customerId) {
        log.debug("Get Customer by Id. Id: " + customerId.toString());
        return customerService.getCustomerById(customerId).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"/", ""})
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customer, HttpServletResponse response) {
        log.debug("Create Customer");
        CustomerDTO savedCustomer = customerService.save(customer);
        response.setHeader("Location", PATH +"/"+savedCustomer.getId());
        return savedCustomer;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable UUID customerId, @RequestBody CustomerDTO customer) {
        log.debug("Update Customer. Id: " + customerId.toString());
        return customerService.update(customerId, customer).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{customerId}")
    public CustomerDTO patchCustomerById(@PathVariable UUID customerId, @RequestBody CustomerDTO customer) {
        log.debug("Patch Customer. Id: " + customerId.toString());
        return customerService.patchById(customerId, customer).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{customerId}")
    public void deleteCustomer(@PathVariable UUID customerId) {
        log.debug("Delete Customer. Id: " + customerId.toString());
        Boolean deleted = customerService.deleteById(customerId);
        if (!deleted) {
            throw new NotFoundException("Customer not found with id: " + customerId);
        }
    }
}

