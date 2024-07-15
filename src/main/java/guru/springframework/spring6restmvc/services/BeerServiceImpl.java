package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author john
 * @since 01/07/2024
 */
@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    public List<BeerDTO> listBeers() {
        log.info("Getting beer list");
        return beerRepository.findAll().stream().map(beerMapper::beerToBeerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BeerDTO> getById(UUID id) {
        log.info("Get Beer by Id - in service. Id: {}", id.toString());
        return beerRepository.findById(id).map(beerMapper::beerToBeerDTO);
    }

    @Override
    public BeerDTO save(BeerDTO beerDTO) {
        log.info("Saving beer to service. Id: {}", beerDTO);
        Beer savedBeer = beerRepository.save(beerMapper.beertDTOtoBeer(beerDTO));
        return beerMapper.beerToBeerDTO(savedBeer);
    }

    @Override
    public Optional<BeerDTO> update(UUID beerId, BeerDTO beer) {
        log.info("Update Beer - in service. Id: {}", beerId);
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
        beerRepository.findById(beerId).ifPresentOrElse(beerFound -> {
            beerFound.setBeerName(beer.getBeerName());
            beerFound.setBeerStyle(beer.getBeerStyle());
            beerFound.setUpc(beer.getUpc());
            beerFound.setPrice(beer.getPrice());
            beerFound.setQuantityOnHand(beer.getQuantityOnHand());
            atomicReference.set(Optional.of(beerMapper.beerToBeerDTO(beerRepository.save(beerFound))));
        }, () -> atomicReference.set(Optional.empty()));

        return atomicReference.get();
    }

    @Override
    public Boolean deleteById(UUID beerId) {
        log.info("Delete Beer - in service. Id: {}", beerId.toString());
       if(beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public void patchById(UUID beerId, BeerDTO beer) {
        log.debug("Patch Beer - in service. Id: " + beerId.toString());
        Beer existingBeer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

        if (beer.getBeerName() != null) {
            existingBeer.setBeerName(beer.getBeerName());
        }
        if (beer.getBeerStyle() != null) {
            existingBeer.setBeerStyle(beer.getBeerStyle());
        }
        if (beer.getUpc() != null) {
            existingBeer.setUpc(beer.getUpc());
        }
        if (beer.getPrice() != null) {
            existingBeer.setPrice(beer.getPrice());
        }
        if (beer.getQuantityOnHand() != null) {
            existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
        }
        beerRepository.save(existingBeer);
    }

    @Override
    public long count() {
        return beerRepository.count();
    }

}
