package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author john
 * @since 26/08/2024
 */
@Slf4j
@SpringBootTest
public class BeerControllerMVCIT {

    public static final String USERNAME = "restadmin";
    public static final String PASSWORD = "password";
    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void listBeers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH)
                        //.with(httpBasic(USERNAME, PASSWORD)))
                        .with(jwt().jwt(jwt -> jwt.notBefore(Instant.now().minusSeconds(5L)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.sorted").value(true))
                .andExpect(jsonPath("$.first").value(true))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    void listBeersSecondPage() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(
                UriComponentsBuilder.fromPath(BeerController.PATH)
                        .queryParam("pageNumber", 2).build().toUri())
                       // .with(httpBasic(USERNAME, PASSWORD))
                        .with(jwt())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.sorted").value(true))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(false))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    void ListBeersByName() throws Exception {
        URI uri =  UriComponentsBuilder.fromPath(BeerController.PATH)
                .queryParam("beerName", "IPA").build().toUri();
        mockMvc.perform(MockMvcRequestBuilders.get(uri.toString())
                        //.with(httpBasic(USERNAME, PASSWORD)))
                        .with(jwt())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
               .andExpect(jsonPath("$.content[0].beerName").value(Matchers.containsStringIgnoringCase("IPA")))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    void listBeersByStyle() throws Exception {
        long count = beerRepository.findByBeerStyle(BeerStyle.WHEAT, PageRequest.of(0, 10))
                .stream().count();

        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH)
                        .param("beerStyle", BeerStyle.WHEAT.name())
                        //.with(httpBasic(USERNAME, PASSWORD)))
                        .with(jwt())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.content.length()").value(Math.toIntExact(count)))
                .andExpect(jsonPath("$.content[0].beerStyle").value(BeerStyle.WHEAT.name()))
                .andExpect(jsonPath("$.content[1].beerStyle").value(BeerStyle.WHEAT.name()))
                .andExpect(jsonPath("$.content[2].beerStyle").value(BeerStyle.WHEAT.name()))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Transactional
    @Test
    void getById() throws Exception {
        Beer first = beerRepository.findAll(PageRequest.of(0, 5)).getContent().getFirst();
        log.info("Beer: {}", first);
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH + "/" + first.getId())
                //        .with(httpBasic(USERNAME, PASSWORD))
                                .with(jwt())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beerName").value(first.getBeerName()))
                .andExpect(jsonPath("$.beerStyle").value(first.getBeerStyle().name()))
                .andExpect(jsonPath("$.price").value(first.getPrice().doubleValue()))
                .andExpect(jsonPath("$.id").value(first.getId().toString()))
                .andExpect(jsonPath("$.upc").value(first.getUpc()))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()));
    }

    @Test
    void authenticateWrongPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer")
                        .with(httpBasic(USERNAME, "wrongpassword"))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticateUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                .andExpect(status().isUnauthorized());
    }
}
