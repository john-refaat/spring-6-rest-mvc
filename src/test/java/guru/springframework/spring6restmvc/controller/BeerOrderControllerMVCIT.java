package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.Customer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvcapi.model.*;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

/**
 * Author:john
 * Date:02/03/2025
 * Time:04:33
 */
@SpringBootTest
public class BeerOrderControllerMVCIT {

    private static final String PATH_BEER_ORDER_ID = BeerOrderController.PATH+"/{beerOrderId}";

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BeerOrderMapper beerOrderMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void listBeerOrders() throws Exception {
        long count = beerOrderRepository.count();
        System.out.println(count);
        int pageSize = 10;
        long contentLength = count<pageSize?count:pageSize;
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(UriComponentsBuilder.fromPath(BeerOrderController.PATH)
                        .queryParam("pageNumber", 2).queryParam("pageSize", 10).build().toUri())
                        .with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(count))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageNumber").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageSize").value(pageSize))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(contentLength))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getBeerOrderById() throws Exception {
        BeerOrder beerOrder = beerOrderRepository.findAll().stream().findFirst().get();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BeerOrderController.PATH + "/" + beerOrder.getId())
                        .with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(beerOrder.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer.name").value(beerOrder.getCustomer().getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines.length()").value(beerOrder.getOrderLines().size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value(beerOrder.getOrderLines().stream().findFirst().get().getBeer().getBeerName()))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createBeerOrder() throws Exception {
        Beer beer = beerRepository.findAll().getFirst();
        Customer customer = customerRepository.findAll().getFirst();
        BeerOrderCreateDTO beerOrder = BeerOrderCreateDTO.builder().customerId(customer.getId())
                .orderLines(Set.of(BeerOrderLineCreateDTO.builder().beerRef(beer.getId()).orderQuantity(2).build()))
                .beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("12345678").build())
                .build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BeerOrderController.PATH)
                .with(jwt())
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(beerOrder)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer.id").value(customer.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.id").value(beer.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value(beer.getBeerName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].orderQuantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerOrderShipment.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerOrderShipment.trackingNumber").value("12345678"))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createBeerOrderCustomerNotFound() throws Exception {
        Beer beer = beerRepository.findAll().getFirst();
        BeerOrderCreateDTO beerOrder = BeerOrderCreateDTO.builder().customerId(UUID.randomUUID())
                .orderLines(Set.of(BeerOrderLineCreateDTO.builder().beerRef(beer.getId()).orderQuantity(1).build()))
                .beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("874345234").build())
                .build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BeerOrderController.PATH)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrder)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn();
        System.out.println(">>>>"+mvcResult.getResponse().getStatus());
    }

    @Test
    void updateBeerOrderUpdateBeerOrderLine() throws Exception {
        List<Beer> beers = beerRepository.findAll();
        BeerOrder savedBeerOrder = saveNewBeerOrder(beers.getFirst());

        BeerOrderDTO beerOrderDTO = beerOrderMapper.beerOrderToBeerOrderDTO(savedBeerOrder);

        beerOrderDTO.setOrderLines(Set.of(BeerOrderLineDTO.builder().beer(beerMapper.beerToBeerDTO(beers.get(1))).build()));

        BeerOrderDTO updatedOrder = BeerOrderDTO.builder().id(beerOrderDTO.getId())
                .orderLines(Set.of(BeerOrderLineDTO.builder().orderQuantity(1).beer(beerMapper.beerToBeerDTO(beers.get(1))).build()))
                .customer(beerOrderDTO.getCustomer()).build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH_BEER_ORDER_ID, beerOrderDTO.getId().toString())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        System.out.println(">>>>"+mvcResult.getResponse().getStatus());
    }

    private BeerOrder saveNewBeerOrder(Beer beer) {
        Customer customer = customerRepository.findAll().getFirst();
        BeerOrder beerOrder = BeerOrder.builder().customer(customer)
                .orderLines(Set.of(BeerOrderLine.builder().orderQuantity(1).beer(beer).build())).build();
        return beerOrderRepository.save(beerOrder);
    }

    @Test
    void updateBeerOrderAddOrderLine() throws Exception {
        List<Beer> beers = beerRepository.findAll();
        BeerOrder savedBeerOrder = saveNewBeerOrder(beers.get(1));

        BeerOrderDTO updatedBeerOrderDTO = beerOrderMapper.beerOrderToBeerOrderDTO(savedBeerOrder);
        Assertions.assertEquals(1, updatedBeerOrderDTO.getOrderLines().size());
        updatedBeerOrderDTO.getOrderLines().add(
                BeerOrderLineDTO.builder().orderQuantity(1).beer(
                        beerMapper.beerToBeerDTO(beers.get(2))).build());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH_BEER_ORDER_ID, updatedBeerOrderDTO.getId().toString())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBeerOrderDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        Assertions.assertEquals(2, beerOrderRepository.findById(updatedBeerOrderDTO.getId()).get().getOrderLines().size());
    }

    @Test
    void updateBeerOrderUpdateQuantity() throws Exception {
        List<Beer> beers = beerRepository.findAll();
        BeerOrder savedBeerOrder = saveNewBeerOrder(beers.getFirst());
        System.out.println(savedBeerOrder);

        BeerOrderDTO beerOrderDTO = beerOrderMapper.beerOrderToBeerOrderDTO(savedBeerOrder);

        BeerOrderLineDTO orderLineDTO = beerOrderDTO.getOrderLines().iterator().next();
        Integer orderQuantity = orderLineDTO.getOrderQuantity();
        orderLineDTO.setOrderQuantity(orderQuantity +1);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(PATH_BEER_ORDER_ID, beerOrderDTO.getId().toString())
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerOrderDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.id").value(beers.getFirst().getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value(beers.getFirst().getBeerName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].orderQuantity").value(orderQuantity+1))

                .andReturn();

        System.out.println(">>>>"+mvcResult.getResponse().getContentAsString());
    }

    @Test
    void updateBeerOrderCheckEveryLine() throws Exception {
        List<Beer> beers = beerRepository.findAll();
        BeerOrder savedBeerOrder = saveNewBeerOrder(beers.getFirst());
        System.out.println(savedBeerOrder);
        BeerOrderDTO beerOrderDTO = beerOrderMapper.beerOrderToBeerOrderDTO(savedBeerOrder);

        Assertions.assertEquals(1, beerOrderDTO.getOrderLines().size());
        beerOrderDTO.getOrderLines().iterator().next().setOrderQuantity(3);
        beerOrderDTO.getOrderLines().add(BeerOrderLineDTO.builder().orderQuantity(1)
                .beer(BeerDTO.builder().id(beers.get(3).getId()).build()).build());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BeerOrderController.PATH+"/2/{beerOrderId}", beerOrderDTO.getId().toString())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        System.out.println(">>>>"+mvcResult.getResponse().getContentAsString());
        beerOrderRepository.flush();
        BeerOrder beerOrderUpdated = beerOrderRepository.findById(beerOrderDTO.getId()).get();
        Assertions.assertEquals(2, beerOrderUpdated.getOrderLines().size());
    }

    @Test
    void deleteBeerOrder() throws Exception {
        BeerOrder beerOrder = saveNewBeerOrder(beerRepository.findAll().getFirst());

        mockMvc.perform(MockMvcRequestBuilders.delete(PATH_BEER_ORDER_ID, beerOrder.getId().toString())
                .with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertTrue(beerOrderRepository.findById(beerOrder.getId()).isEmpty());

        mockMvc.perform(MockMvcRequestBuilders.delete(PATH_BEER_ORDER_ID, beerOrder.getId().toString())
                        .with(jwt()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
