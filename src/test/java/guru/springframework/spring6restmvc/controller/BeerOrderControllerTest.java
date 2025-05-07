package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvcapi.model.*;
import guru.springframework.spring6restmvc.services.BeerOrderServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * Author:john
 * Date:26/02/2025
 * Time:05:00
 */
@ExtendWith(MockitoExtension.class)
class BeerOrderControllerTest {

    @Mock
    BeerOrderServiceImpl beerOrderService;

    @InjectMocks
    BeerOrderController beerOrderController;


    MockMvc mockMvc;
    BeerOrderDTO beerOrderDTO1;
    BeerOrderDTO beerOrderDTO2;

    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(beerOrderController)
                .setControllerAdvice(CustomErrorController.class).build();
        beerOrderDTO1 = BeerOrderDTO.builder().id(UUID.randomUUID())
                .orderLines(Set.of(BeerOrderLineDTO.builder().beer(BeerDTO.builder().beerName("ABC").build()).build()))
                .customer(CustomerDTO.builder().name("John").build())
                .build();
        beerOrderDTO2 = BeerOrderDTO.builder().id(UUID.randomUUID())
                .orderLines(Set.of(BeerOrderLineDTO.builder().beer(BeerDTO.builder().beerName("XYZ").build()).build()))
                .customer(CustomerDTO.builder().name("Alice").build())
                .build();
    }

    @Test
    void listBeerOrders() throws Exception {
        // Given
        Page<BeerOrderDTO> beerOrderList = new PageImpl<>(List.of(beerOrderDTO1, beerOrderDTO2));
        BDDMockito.given(beerOrderService.listOrders(any(), any())).willReturn(beerOrderList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BeerOrderController.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].customer.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].customer.name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].orderLines[0].beer.beerName").value("ABC"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].orderLines[0].beer.beerName").value("XYZ"))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getBeerOrderById() throws Exception {
        BDDMockito.given(beerOrderService.getOrderById(any(UUID.class))).willReturn(Optional.of(beerOrderDTO1));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BeerOrderController.PATH + "/" + beerOrderDTO1.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value("ABC"))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void createBeerOrder() throws Exception {

        BDDMockito.given(beerOrderService.createOrder(any(BeerOrderCreateDTO.class))).willReturn(beerOrderDTO1);
        ArgumentCaptor<BeerOrderCreateDTO> captor = ArgumentCaptor.forClass(BeerOrderCreateDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.post(BeerOrderController.PATH)
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(BeerOrderCreateDTO.builder().customerId(UUID.randomUUID())
                       .orderLines(Set.of(BeerOrderLineCreateDTO.builder().beerRef(UUID.randomUUID()).orderQuantity(2).build())).build())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().stringValues("Location", BeerOrderController.PATH+"/"+beerOrderDTO1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer.name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value("ABC"));
        Mockito.verify(beerOrderService, Mockito.times(1)).createOrder(captor.capture());
        Assertions.assertNotNull(captor.getValue());
        System.out.println(captor.getValue());
    }

    @Test
    void updateBeerOrder() throws Exception {
        beerOrderDTO1.getCustomer().setName("Alice");
        beerOrderDTO1.getOrderLines().iterator().next().getBeer().setBeerName("XYZ");
        BDDMockito.given(beerOrderService.updateOrder(any(UUID.class), any(BeerOrderDTO.class))).willReturn(Optional.of(beerOrderDTO1));
        ArgumentCaptor<BeerOrderDTO> captor = ArgumentCaptor.forClass(BeerOrderDTO.class);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(BeerOrderController.PATH + "/" + beerOrderDTO1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":{\"name\":\"Alice\"},\"orderLines\":[{\"beer\":{\"beerName\":\"XYZ\"}}]}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customer.name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderLines[0].beer.beerName").value("XYZ"))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        Mockito.verify(beerOrderService, Mockito.times(1)).updateOrder(any(UUID.class), captor.capture());
        Assertions.assertNotNull(captor.getValue());
        Assertions.assertEquals("Alice", captor.getValue().getCustomer().getName());
        Assertions.assertEquals("XYZ", captor.getValue().getOrderLines().iterator().next().getBeer().getBeerName());

    }

    @Test
    void patchBeerOrder() throws Exception {
        BeerOrderDTO beerOrderDTO = BeerOrderDTO.builder().id(UUID.randomUUID()).customer(CustomerDTO.builder().name("Bob").build())
                .beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("12345").build()).build();
        ArgumentCaptor<BeerOrderDTO> captor = ArgumentCaptor.forClass(BeerOrderDTO.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(BeerOrderController.PATH + "/" + beerOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":{\"name\":\"Bob\"},\"beerOrderShipment\":{\"trackingNumber\":\"12345\"}}"));
        Mockito.verify(beerOrderService, Mockito.times(1)).patchOrder(any(UUID.class), captor.capture());
        Assertions.assertNotNull(captor.getValue());
        Assertions.assertEquals("Bob", captor.getValue().getCustomer().getName());
        Assertions.assertEquals("12345", captor.getValue().getBeerOrderShipment().getTrackingNumber());
    }

    @Test
    void patchBeerOrderNoCustomer() throws Exception {
        BeerOrderDTO beerOrderDTO = BeerOrderDTO.builder().id(UUID.randomUUID()).beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("12345").build()).build();
        ArgumentCaptor<BeerOrderDTO> captor = ArgumentCaptor.forClass(BeerOrderDTO.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(BeerOrderController.PATH + "/" + beerOrderDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"beerOrderShipment\":{\"trackingNumber\":\"12345\"}}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(beerOrderService, Mockito.times(1)).patchOrder(any(UUID.class), captor.capture());
        Assertions.assertNotNull(captor.getValue());
        Assertions.assertEquals("12345", captor.getValue().getBeerOrderShipment().getTrackingNumber());
    }

    @Test
    void deleteBeerOrder() throws Exception {
        BDDMockito.given(beerOrderService.deleteOrder(any(UUID.class))).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BeerOrderController.PATH + "/" + beerOrderDTO1.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(beerOrderService, Mockito.times(1)).deleteOrder(captor.capture());
        Assertions.assertEquals(beerOrderDTO1.getId(), captor.getValue());

    }

    @Test
    void createBeerOrderNoCustomer() throws Exception {
        BeerOrderDTO beerOrderDTO = BeerOrderDTO.builder().id(UUID.randomUUID()).beerOrderShipment(BeerOrderShipmentDTO.builder().trackingNumber("12345").build()).build();
        ArgumentCaptor<BeerOrderDTO> captor = ArgumentCaptor.forClass(BeerOrderDTO.class);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(BeerOrderController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"beerOrderShipment\":{\"trackingNumber\":\"12345\"}}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", Matchers.equalTo("customerId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", Matchers.equalTo("An order must be related to a customer")))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        Mockito.verify(beerOrderService, Mockito.times(0)).createOrder(captor.capture());
    }

}

