package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author john
 * @since 10/07/2024
 */
@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);
    Beer beertDTOtoBeer(BeerDTO beerDTO);
    BeerDTO beerToBeerDTO(Beer beer);
}
