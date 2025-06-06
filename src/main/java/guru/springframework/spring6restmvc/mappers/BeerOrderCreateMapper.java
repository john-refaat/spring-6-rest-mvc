package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvcapi.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineCreateDTO;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Author:john
 * Date:03/03/2025
 * Time:05:40
 */
@Slf4j
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring")
public abstract class BeerOrderCreateMapper {

    public static BeerOrderCreateMapper INSTANCE = Mappers.getMapper(BeerOrderCreateMapper.class);

    private CustomerRepository customerRepository;
    private BeerRepository beerRepository;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository, BeerRepository beerRepository) {
        this.customerRepository = customerRepository;
        this.beerRepository = beerRepository;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target= "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    abstract BeerOrder toEntity(BeerOrderCreateDTO beerOrderDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "beer", ignore = true)
    @Mapping(target= "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "quantityAllocated", ignore = true)
    @Mapping(target = "beerOrder", ignore = true)
    abstract BeerOrderLine toBeerOrderLineEntity(BeerOrderLineCreateDTO beerOrderLineCreateDTO);

    public BeerOrder beerOrderCreateDTOToBeerOrder(BeerOrderCreateDTO beerOrderCreateDTO) {
        log.info("Converting BeerOrderCreateDTO to BeerOrder");
        BeerOrder beerOrder = toEntity(beerOrderCreateDTO);
        Customer customer = customerRepository.findById(beerOrderCreateDTO.getCustomerId()).orElseThrow(NotFoundException::new);
        log.info("Found Customer: {}", customer.getName());
        beerOrder.setCustomer(customer);
        Set<BeerOrderLine> orderLines = new HashSet<>();
        beerOrderCreateDTO.getOrderLines().forEach(line ->
                orderLines.add(beerOrderLineCreateDTOToBeerOrderLine(line)));
        beerOrder.setOrderLines(orderLines);
        log.info("BeerOrder Object: {}", beerOrder);
        return beerOrder;
    }

    public BeerOrderLine beerOrderLineCreateDTOToBeerOrderLine(BeerOrderLineCreateDTO beerOrderLineCreateDTO) {
        BeerOrderLine beerOrderLine = toBeerOrderLineEntity(beerOrderLineCreateDTO);
        Beer beer = beerRepository.findById(beerOrderLineCreateDTO.getBeerRef()).orElseThrow(NotFoundException::new);
        log.info("Found Beer: {}", beer.getBeerName());
        beerOrderLine.setBeer(beer);
        return beerOrderLine;
    }
}
