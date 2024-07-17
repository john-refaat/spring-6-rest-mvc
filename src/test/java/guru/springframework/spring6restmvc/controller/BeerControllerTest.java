package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author john
 * @since 05/07/2024
 */
@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    private BeerDTO fooBeer;
    private BeerDTO barBeer;

    @Spy
    @InjectMocks
    BeerController controller;
    @Mock
    BeerService beerService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(CustomErrorController.class).build();
        fooBeer = BeerDTO.builder().id(UUID.randomUUID()).beerName("foo")
                .beerStyle(BeerStyle.WHEAT).upc("23456780987").price(BigDecimal.TEN).build();
        barBeer = BeerDTO.builder().id(UUID.randomUUID()).beerName("bar")
                .beerStyle(BeerStyle.LAGER).upc("98765432109").price(BigDecimal.valueOf(15)).build();
    }

    @Test
    void listBeers() throws Exception {
        //given
        Mockito.when(beerService.listBeers()).thenReturn(List.of(fooBeer, barBeer));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].beerName").value("foo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].beerName").value("bar"));

        //then
        Mockito.verify(beerService, Mockito.times(1)).listBeers();
    }

    @Test
    void getById() throws Exception {
        //given
        BDDMockito.given(beerService.getById(ArgumentMatchers.any(UUID.class))).willReturn(Optional.of(fooBeer));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH + "/" + fooBeer.getId()))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value("foo"));

        //then
        Mockito.verify(beerService, Mockito.times(1)).getById(fooBeer.getId());
    }

    @Test
    void save() throws Exception {
        // given
        BDDMockito.given(beerService.save(ArgumentMatchers.any(BeerDTO.class))).willReturn(fooBeer);
        BeerDTO savedBeer = BeerDTO.builder().beerName(fooBeer.getBeerName()).beerStyle(fooBeer.getBeerStyle())
                .upc(fooBeer.getUpc()).price(fooBeer.getPrice()).build();

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(BeerController.PATH)
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(savedBeer)))
               .andExpect(MockMvcResultMatchers.status().isCreated())
               .andExpect(MockMvcResultMatchers.header().string("Location", BeerController.PATH + "/" + fooBeer.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(fooBeer.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value(fooBeer.getBeerName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerStyle").value(fooBeer.getBeerStyle().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.upc").value(fooBeer.getUpc()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(fooBeer.getPrice().toString()));


        // then
        Mockito.verify(beerService, Mockito.times(1)).save(savedBeer);
    }

    @Test
    void createWithoutName() throws Exception {
        // given
       fooBeer.setBeerName(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(BeerController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(fooBeer)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field").value("beerName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("Beer Name is required"));

        // then
        Mockito.verify(beerService, Mockito.times(0)).save(ArgumentMatchers.any(BeerDTO.class));
    }

    @Test
    void createWithoutNameStyleUPCPrice() throws Exception {
        BeerDTO newBeerDTO = BeerDTO.builder().build();
        // when
        mockMvc.perform(MockMvcRequestBuilders.post(BeerController.PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(newBeerDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    void update() throws Exception {
        // given
        BeerDTO updatedBeer = fooBeer;
        updatedBeer.setBeerName("updated");
        BDDMockito.given(beerService.update(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(BeerDTO.class)))
                .willReturn(Optional.of(updatedBeer));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(updatedBeer)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(fooBeer.getId().toString()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value(updatedBeer.getBeerName()));

        // then
        Mockito.verify(beerService, Mockito.times(1)).update(ArgumentMatchers.eq(fooBeer.getId()),
                ArgumentMatchers.eq(updatedBeer));
    }

    @Test
    void updateWithoutName() throws Exception {
        // given
        BeerDTO beerWithoutName = BeerDTO.builder().id(fooBeer.getId()).build();

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(beerWithoutName)))
               .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // then
        Mockito.verify(beerService, Mockito.times(0)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));

    }

    @Test
    void updateWithoutBeerStyle() throws Exception {
        // given
        fooBeer.setBeerStyle(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(fooBeer)))
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field").value("beerStyle"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("Beer Style is required"));

        // then
        Mockito.verify(beerService, Mockito.times(0)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));
    }

    @Test
    void updateWithoutUPC() throws Exception {
        // given
        fooBeer.setUpc(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(fooBeer)))
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field").value("upc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("UPC is required"));

        // then
        Mockito.verify(beerService, Mockito.times(0)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));
    }

    @Test
    void updateWithoutPrice() throws Exception {
        // given
        fooBeer.setPrice(null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(fooBeer)))
               .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field").value("price"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message").value("Price is required"));

        // then
        Mockito.verify(beerService, Mockito.times(0)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));
    }

    @Test
    void updateNotFound() throws Exception {
        // given
        fooBeer.setId(UUID.randomUUID());
        BDDMockito.given(beerService.update(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(BeerDTO.class)))
                .willReturn(Optional.empty());

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(fooBeer)))
               .andExpect(MockMvcResultMatchers.status().isNotFound());

        // then
        Mockito.verify(beerService, Mockito.times(1)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));
    }

    @Test
    void patchBeerById() throws Exception {
        // given
        BeerDTO patchedBeer = BeerDTO.builder().id(fooBeer.getId()).beerName("updated").build();

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch(BeerController.PATH + "/" + fooBeer.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(patchedBeer)))
               .andExpect(MockMvcResultMatchers.status().isNoContent());

        // then
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<BeerDTO> beerCaptor = ArgumentCaptor.forClass(BeerDTO.class);
        Mockito.verify(beerService, Mockito.times(1)).patchById(uuidCaptor.capture(), beerCaptor.capture());
        Assertions.assertThat(uuidCaptor.getValue()).isEqualTo(fooBeer.getId());
        Assertions.assertThat(beerCaptor.getValue()).isEqualTo(patchedBeer);
    }

    @Test
    void deleteBeer() throws Exception {
        // given
        BDDMockito.given(beerService.deleteById(ArgumentMatchers.any(UUID.class))).willReturn(true);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(BeerController.PATH + "/" + fooBeer.getId()))
               .andExpect(MockMvcResultMatchers.status().isNoContent());

        // then
        Mockito.verify(beerService, Mockito.times(1)).deleteById(fooBeer.getId());
    }

    @Test
    void deleteBeerNotFound() throws Exception {
        // given
        BDDMockito.given(beerService.deleteById(ArgumentMatchers.any(UUID.class))).willReturn(false);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(BeerController.PATH + "/" + UUID.randomUUID()))
               .andExpect(MockMvcResultMatchers.status().isNotFound());

        // then
        Mockito.verify(beerService, Mockito.times(1)).deleteById(ArgumentMatchers.any(UUID.class));
    }

    @Test
    void getBeerbyIdNotFound() throws Exception {
        // given
        BDDMockito.given(beerService.getById(ArgumentMatchers.any(UUID.class))).willReturn(Optional.empty());

        // when
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH + "/" + UUID.randomUUID()))
               .andExpect(MockMvcResultMatchers.status().isNotFound());

        // then
        Mockito.verify(beerService, Mockito.times(1)).getById(ArgumentMatchers.any(UUID.class));
    }

    private String asJsonString(BeerDTO beer) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(beer);
    }
}