package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvcapi.model.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author john
 * @since 11/07/2024
 */
@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);
    CustomerDTO customerToCustomerDTO(Customer customer);
    Customer customerDTOToCustomer(CustomerDTO customerDTO);
}
