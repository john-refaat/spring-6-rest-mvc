package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderLineMapper;
import guru.springframework.spring6restmvc.repository.BeerOrderLineRepository;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Author:john
 * Date:08/05/2025
 * Time:02:35
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderLineServiceImpl implements BeerOrderLineService {

    private final BeerOrderLineRepository beerOrderLineRepository;
    private final BeerOrderLineMapper beerOrderLineMapper;
    private final BeerMapper beerMapper;

    @Override
    public BeerOrderLineDTO findById(UUID id) {
        log.info("Finding BeerOrderLine by ID: {}", id);
        return beerOrderLineRepository.findById(id)
                .map(beerOrderLineMapper::beerOrderLineToBeerOrderLineDTO)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    @Override
    public BeerOrderLineDTO save(BeerOrderLineDTO beerOrderLineDTO) {
        log.info("Saving BeerOrderLine: {}", beerOrderLineDTO);
        return beerOrderLineMapper.beerOrderLineToBeerOrderLineDTO(
                beerOrderLineRepository.save(beerOrderLineMapper.beerOrderLineDTOToBeerOrderLine(beerOrderLineDTO)));
    }

    @Transactional
    @Override
    public BeerOrderLineDTO update(UUID id, BeerOrderLineDTO beerOrderLineDTO) {
        log.info("Updating BeerOrderLine: {}", beerOrderLineDTO);
        return beerOrderLineRepository.findById(id)
                .map(foundOrderLine -> {
            foundOrderLine.setBeer(beerMapper.beerDTOToBeer(beerOrderLineDTO.getBeer()));
            foundOrderLine.setOrderQuantity(beerOrderLineDTO.getOrderQuantity());
            foundOrderLine.setStatus(beerOrderLineDTO.getStatus());
            foundOrderLine.setQuantityAllocated(beerOrderLineDTO.getQuantityAllocated());
            return foundOrderLine;
        }).map(beerOrderLineRepository::save)
                .map(beerOrderLineMapper::beerOrderLineToBeerOrderLineDTO)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        log.info("Deleting BeerOrderLine by ID: {}", id);
        if(beerOrderLineRepository.existsById(id))
            beerOrderLineRepository.deleteById(id);
        else
            throw new NotFoundException("BeerOrderLine not found with ID: " + id);
    }
}
