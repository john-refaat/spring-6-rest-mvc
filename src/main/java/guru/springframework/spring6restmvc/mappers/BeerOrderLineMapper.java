package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.model.BeerOrderLineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:31
 */
@Mapper
public interface BeerOrderLineMapper {

    BeerOrderLineMapper INSTANCE = Mappers.getMapper(BeerOrderLineMapper.class);

    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDTOToBeerOrderLine(BeerOrderLineDTO beerOrderLineDTO);
    BeerOrderLineDTO beerOrderLineToBeerOrderLineDTO(BeerOrderLine beerOrderLine);
}
