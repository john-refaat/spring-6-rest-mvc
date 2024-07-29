package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author john
 * @since 11/07/2024
 */
@ExtendWith(MockitoExtension.class)
class BeerServiceImplTest {

    public static final Beer beer1 = Beer.builder().id(UUID.randomUUID()).beerName("My Beer").build();
    public static final Beer beer2 = Beer.builder().id(UUID.randomUUID()).beerName("Stella").build();
    public static final String RISE = "rise";
    private BeerServiceImpl beerService;
    @Mock
    private BeerRepository beerRepository;

    @BeforeEach
    void setUp() {
        beerService= new BeerServiceImpl(beerRepository, BeerMapper.INSTANCE);
    }

    @Test
    void listBeers() {
        // Given
        List<Beer> beerDTOList = List.of(beer1, beer2);
        given(beerRepository.findAll()).willReturn(beerDTOList);

        // When
        List<BeerDTO> beerDTOS = beerService.listBeers(Optional.empty(), Optional.empty());

        // Then
        Assertions.assertThat(beerDTOS.size()).isEqualTo(2);
        Assertions.assertThat(beerDTOS.get(0).getBeerName()).isEqualTo(beer1.getBeerName());
        Assertions.assertThat(beerDTOS.get(1).getBeerName()).isEqualTo(beer2.getBeerName());
    }

    @Test
    void listBeersByBeerName() {
        // Given
        given(beerRepository.findByBeerNameLikeIgnoreCase(anyString())).willReturn(List.of(beer1));

        // When
        List<BeerDTO> beerDTOS = beerService.listBeers(Optional.of(RISE), Optional.empty());

        // Then
        Assertions.assertThat(beerDTOS.size()).isEqualTo(1);
        Assertions.assertThat(beerDTOS.get(0).getBeerName()).isEqualTo(beer1.getBeerName());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(beerRepository).findByBeerNameLikeIgnoreCase(captor.capture());
        Assertions.assertThat(captor.getValue()).isEqualTo("%"+RISE+"%");
    }

    @Test
    void listBeersByBeerStyle() {
        // Given
        given(beerRepository.findByBeerStyle(any(BeerStyle.class))).willReturn(List.of(beer1));

        // When
        List<BeerDTO> beerDTOS = beerService.listBeers(Optional.empty(), Optional.of(BeerStyle.WHEAT));

        // Then
        Assertions.assertThat(beerDTOS.size()).isEqualTo(1);
        Assertions.assertThat(beerDTOS.get(0).getBeerName()).isEqualTo(beer1.getBeerName());
        ArgumentCaptor<BeerStyle> captor = ArgumentCaptor.forClass(BeerStyle.class);
        verify(beerRepository).findByBeerStyle(captor.capture());
        Assertions.assertThat(captor.getValue()).isEqualTo(BeerStyle.WHEAT);
    }

    @Test
    void listBeersByBeerNameAndBeerStyle() {
        // Given
        given(beerRepository.findByBeerNameLikeIgnoreCaseAndBeerStyle(anyString(), any(BeerStyle.class))).willReturn(List.of(beer2));

        // When
        List<BeerDTO> beerDTOS = beerService.listBeers(Optional.of(RISE), Optional.of(BeerStyle.WHEAT));

        // Then
        Assertions.assertThat(beerDTOS.size()).isEqualTo(1);
        Assertions.assertThat(beerDTOS.get(0).getBeerName()).isEqualTo(beer2.getBeerName());
        ArgumentCaptor<String> captorName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BeerStyle> captorStyle = ArgumentCaptor.forClass(BeerStyle.class);
        verify(beerRepository).findByBeerNameLikeIgnoreCaseAndBeerStyle(captorName.capture(), captorStyle.capture());
        Assertions.assertThat(captorName.getValue()).isEqualTo("%"+RISE+"%");
        Assertions.assertThat(captorStyle.getValue()).isEqualTo(BeerStyle.WHEAT);
    }

    @Test
    void getById() {
        // Given
        given(beerRepository.findById(beer1.getId())).willReturn(Optional.of(beer1));

        // When
        BeerDTO beerDTO = beerService.getById(beer1.getId()).get();

        // Then
        Assertions.assertThat(beerDTO.getBeerName()).isEqualTo(beer1.getBeerName());
    }

    @Test
    void save() {
        // Given
        BeerDTO beerDTO = BeerDTO.builder().beerName(beer1.getBeerName()).build();
        given(beerRepository.save(any(Beer.class))).willReturn(beer1);

        // When
        BeerDTO savedBeerDTO = beerService.save(beerDTO);

        // Then
        Assertions.assertThat(savedBeerDTO).isNotNull();
        Assertions.assertThat(savedBeerDTO.getId()).isNotNull();
        Assertions.assertThat(savedBeerDTO.getBeerName()).isEqualTo(beer1.getBeerName());
    }

    @Test
    void saveNameTooLong() throws Exception {
        // Given
        BeerDTO beerDTO = BeerDTO.builder().beerName("a".repeat(51))
                .beerStyle(BeerStyle.WHEAT).price(BigDecimal.TEN).upc("111").build();
        given(beerRepository.save(any(Beer.class))).willThrow(ConstraintViolationException.class);
        
        // When
        assertThrows(ConstraintViolationException.class,
                // Then
                () -> beerService.save(beerDTO));
    }

    @Test
    void update() {
        // Given
        BeerDTO beerDTO = BeerDTO.builder().id(beer1.getId()).beerName("updated").build();
        Beer updatedBeer = Beer.builder().id(beer1.getId()).beerName("updated").build();
        given(beerRepository.findById(any(UUID.class))).willReturn(Optional.of(beer1));
        given(beerRepository.save(any(Beer.class))).willReturn(updatedBeer);

        // When
        BeerDTO updatedBeerDTO = beerService.update(beerDTO.getId(), beerDTO).get();

        // Then
        Assertions.assertThat(updatedBeerDTO).isNotNull();
        Assertions.assertThat(updatedBeerDTO.getId()).isEqualTo(updatedBeer.getId());
        Assertions.assertThat(updatedBeerDTO.getBeerName()).isEqualTo(updatedBeer.getBeerName());
    }

    @Test
    void deleteById() {
        // Given
        given(beerRepository.existsById(any(UUID.class))).willReturn(true);

        // When
        boolean deleted = beerService.deleteById(beer1.getId());

        // Then
        Assertions.assertThat(deleted).isTrue();
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(beerRepository, times(1)).deleteById(captor.capture());
        Assertions.assertThat(captor.getValue()).isEqualTo(beer1.getId());
    }

    @Test
    void deleteByIdNotFound(){
        // Given
        given(beerRepository.existsById(any(UUID.class))).willReturn(false);

        // When
        boolean deleted = beerService.deleteById(beer1.getId());

        // Then
        Assertions.assertThat(deleted).isFalse();
        verify(beerRepository, never()).deleteById(any(UUID.class));
    }


    @Test
    void patchById() {
        // Given
        BeerDTO beerDTO = BeerDTO.builder().id(beer1.getId()).beerName("updated").build();
        Beer updatedBeer = Beer.builder().id(beer1.getId()).beerName("updated").build();
        given(beerRepository.findById(any(UUID.class))).willReturn(Optional.of(beer1));
        given(beerRepository.save(any(Beer.class))).willReturn(updatedBeer);

        // When
       beerService.patchById(beerDTO.getId(), beerDTO);

        // Then
        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerRepository, times(1)).findById(uuidArgumentCaptor.capture());
        Assertions.assertThat(uuidArgumentCaptor.getValue()).isEqualTo(beerDTO.getId());
    }
}