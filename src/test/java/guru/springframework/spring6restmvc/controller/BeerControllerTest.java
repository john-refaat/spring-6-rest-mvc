package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author john
 * @since 05/07/2024
 */
@ExtendWith(MockitoExtension.class)
class BeerControllerTest {

    public static final BeerDTO FOO_BEER = BeerDTO.builder().id(UUID.randomUUID()).beerName("foo").build();
    public static final BeerDTO BAR_BEER = BeerDTO.builder().id(UUID.randomUUID()).beerName("bar").build();
    @InjectMocks
    BeerController controller;
    @Mock
    BeerService beerService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listBeers() throws Exception {
        //given
        Mockito.when(beerService.listBeers()).thenReturn(List.of(FOO_BEER, BAR_BEER));

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
        BDDMockito.given(beerService.getById(ArgumentMatchers.any(UUID.class))).willReturn(Optional.of(FOO_BEER));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get(BeerController.PATH + "/" + FOO_BEER.getId()))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value("foo"));

        //then
        Mockito.verify(beerService, Mockito.times(1)).getById(FOO_BEER.getId());
    }

    @Test
    void save() throws Exception {
        // given
        BDDMockito.given(beerService.save(ArgumentMatchers.any(BeerDTO.class))).willReturn(FOO_BEER);

        // when
        BeerDTO savedBeer = BeerDTO.builder().beerName("foo").build();
        mockMvc.perform(MockMvcRequestBuilders.post(BeerController.PATH)
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(savedBeer)))
               .andExpect(MockMvcResultMatchers.status().isCreated())
               .andExpect(MockMvcResultMatchers.header().string("Location", BeerController.PATH + "/" + FOO_BEER.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(FOO_BEER.getId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value(FOO_BEER.getBeerName()));

        // then
        Mockito.verify(beerService, Mockito.times(1)).save(savedBeer);
    }

    @Test
    void update() throws Exception {
        // given
        BeerDTO updatedBeer = BeerDTO.builder().id(FOO_BEER.getId()).beerName("updated").build();
        BDDMockito.given(beerService.update(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(BeerDTO.class)))
                .willReturn(Optional.of(updatedBeer));

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + FOO_BEER.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(updatedBeer)))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(FOO_BEER.getId().toString()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.beerName").value(updatedBeer.getBeerName()));

        // then
        Mockito.verify(beerService, Mockito.times(1)).update(ArgumentMatchers.eq(FOO_BEER.getId()),
                ArgumentMatchers.eq(updatedBeer));
    }

    @Test
    void updateNotFound() throws Exception {
        // given
        BDDMockito.given(beerService.update(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(BeerDTO.class)))
                .willReturn(Optional.empty());

        // when
        mockMvc.perform(MockMvcRequestBuilders.put(BeerController.PATH + "/" + UUID.randomUUID())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(BeerDTO.builder().beerName("updated").build())))
               .andExpect(MockMvcResultMatchers.status().isNotFound());

        // then
        Mockito.verify(beerService, Mockito.times(1)).update(ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.any(BeerDTO.class));
    }
    @Test
    void patchBeerById() throws Exception {
        // given
        BeerDTO patchedBeer = BeerDTO.builder().id(FOO_BEER.getId()).beerName("updated").build();

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch(BeerController.PATH + "/" + FOO_BEER.getId())
               .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
               .content(asJsonString(patchedBeer)))
               .andExpect(MockMvcResultMatchers.status().isNoContent());

        // then
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<BeerDTO> beerCaptor = ArgumentCaptor.forClass(BeerDTO.class);
        Mockito.verify(beerService, Mockito.times(1)).patchById(uuidCaptor.capture(), beerCaptor.capture());
        Assertions.assertThat(uuidCaptor.getValue()).isEqualTo(FOO_BEER.getId());
        Assertions.assertThat(beerCaptor.getValue()).isEqualTo(patchedBeer);
    }

    @Test
    void deleteBeer() throws Exception {
        // given
        BDDMockito.given(beerService.deleteById(ArgumentMatchers.any(UUID.class))).willReturn(true);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete(BeerController.PATH + "/" + FOO_BEER.getId()))
               .andExpect(MockMvcResultMatchers.status().isNoContent());

        // then
        Mockito.verify(beerService, Mockito.times(1)).deleteById(FOO_BEER.getId());
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