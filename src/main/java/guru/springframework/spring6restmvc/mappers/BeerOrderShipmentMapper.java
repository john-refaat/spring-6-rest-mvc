package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.BeerOrderShipment;
import guru.springframework.spring6restmvcapi.model.BeerOrderShipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:36
 */
@Mapper
public interface BeerOrderShipmentMapper {
    BeerOrderShipmentMapper INSTANCE = Mappers.getMapper(BeerOrderShipmentMapper.class);

    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDTOToBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDTO);
    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDTO(BeerOrderShipment beerOrderShipment);

}