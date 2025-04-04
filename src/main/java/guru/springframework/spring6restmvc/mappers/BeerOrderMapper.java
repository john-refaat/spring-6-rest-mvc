package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:38
 */
@Mapper
public interface BeerOrderMapper {
    BeerOrderMapper INSTANCE = Mappers.getMapper(BeerOrderMapper.class);

    BeerOrder beerOrderDTOToBeerOrder(BeerOrderDTO beerOrderDTO);
    BeerOrderDTO beerOrderToBeerOrderDTO(BeerOrder beerOrder);

}
