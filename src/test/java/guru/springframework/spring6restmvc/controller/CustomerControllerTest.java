package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.config.SpringSecurityConfig;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author john
 * @since 05/07/2024
 */
@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfig.class)
class CustomerControllerTest {

    public static final CustomerDTO JOHN = CustomerDTO.builder().id(UUID.randomUUID()).name("John").build();
    public static final CustomerDTO JANE = CustomerDTO.builder().id(UUID.randomUUID()).name("Jane").build();
    public static final String BASE_PATH = "/api/v1/customer";
    public static final String USERNAME = "restadmin";
    public static final String PASSWORD = "password";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;

    @BeforeEach
    void setUp() {
    }

    private String asJsonString(CustomerDTO customer) throws JsonProcessingException {
        return objectMapper.writeValueAsString(customer);
    }

    @Test
    void listCustomers() throws Exception {
        // Given
        Mockito.when(customerService.listCustomers()).thenReturn(List.of(JOHN, JANE));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH)
                //.with(httpBasic(USERNAME, PASSWORD)))
                                .with(jwt()))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(JOHN.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(JANE.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(JOHN.getName()))
                .andExpect(jsonPath("$[1].name").value(JANE.getName()));
        Mockito.verify(customerService, Mockito.times(1)).listCustomers();
    }

    @Test
    void getCustomerById() throws Exception {
        // Given
        Mockito.when(customerService.getCustomerById(JOHN.getId())).thenReturn(Optional.of(JOHN));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + JOHN.getId())
                        //.with(httpBasic(USERNAME, PASSWORD))
                        .with(jwt())
                )
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(JOHN.getName()));
        // Then
        Mockito.verify(customerService, Mockito.times(1)).getCustomerById(JOHN.getId());
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        // given
        Mockito.when(customerService.getCustomerById(JOHN.getId())).thenReturn(Optional.empty());

        // when
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + UUID.randomUUID())
                        //.with(httpBasic(USERNAME, PASSWORD)))
                                .with(jwt()))
                // then
                .andExpect(status().isNotFound());

        Mockito.verify(customerService, Mockito.times(1)).getCustomerById(ArgumentMatchers.any(UUID.class));
    }

    @Test
        void createCustomer() throws Exception {
        // given
        CustomerDTO newCustomer = CustomerDTO.builder().name("John").build();
        BDDMockito.given(customerService.save(ArgumentMatchers.any(CustomerDTO.class))).willReturn(JOHN);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                        //.with(httpBasic(USERNAME, PASSWORD))
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(JOHN.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(newCustomer.getName()));

        // then
        Mockito.verify(customerService, Mockito.times(1)).save(ArgumentMatchers.any(CustomerDTO.class));
    }

    @Test
    void updateCustomer() throws Exception {
        // given
        CustomerDTO updatedCustomer = CustomerDTO.builder().id(JOHN.getId()).name("John Updated").build();
        BDDMockito.given(customerService.update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(CustomerDTO.class))).willReturn(Optional.of(updatedCustomer));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + JOHN.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedCustomer))
                        //.with(httpBasic(USERNAME, PASSWORD)))
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(JOHN.getId().toString()))
                .andExpect(jsonPath("$.name").value(updatedCustomer.getName()));

        // then
        Mockito.verify(customerService, Mockito.times(1)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(CustomerDTO.class));
    }

    @Test
    void patchCustomerById() throws Exception {
        // given
        CustomerDTO updatedCustomer = CustomerDTO.builder().id(JOHN.getId()).name("John Updated").build();
        BDDMockito.given(customerService.patchById(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(CustomerDTO.class))).willReturn(Optional.of(updatedCustomer));

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/" + JOHN.getId())
                       // .with(httpBasic(USERNAME, PASSWORD))
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedCustomer)))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(JOHN.getId().toString()))
            .andExpect(jsonPath("$.name").value(updatedCustomer.getName()));

        // then
        Mockito.verify(customerService, Mockito.times(1)).patchById(ArgumentMatchers.eq(JOHN.getId()),
                ArgumentMatchers.eq(updatedCustomer));
    }

    @Test
    void deleteCustomer() throws Exception {
        // given
        BDDMockito.given(customerService.deleteById(JOHN.getId())).willReturn(true);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + JOHN.getId())
                        //.with(httpBasic(USERNAME, PASSWORD)))
                        .with(jwt()))
                .andExpect(status().isNoContent());

        // then
        Mockito.verify(customerService, Mockito.times(1)).deleteById(JOHN.getId());
    }
}