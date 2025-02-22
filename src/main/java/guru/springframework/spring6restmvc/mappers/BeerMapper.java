package guru.springframework.spring6restmvc.mappers;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerAudit;
import guru.springframework.spring6restmvc.model.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "auditCreatedDate", ignore = true)
    @Mapping(target = "principalName", ignore = true)
    @Mapping(target = "auditEventType", ignore = true)
    BeerAudit beerToBeerAudit(Beer beer);
}
