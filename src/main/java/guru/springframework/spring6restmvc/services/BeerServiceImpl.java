package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerSearchCriteria;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author john
 * @since 01/07/2024
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final CacheManager cacheManager;


    @Cacheable(cacheNames = "beerListCache")
    @Override
    public Page<BeerDTO> listBeers(Optional<String> beerName, Optional<BeerStyle> beerStyle, Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        log.info("List Beers - in service");

        PageRequest pageRequest = PageRequest.of(pageNumber.orElse(1) - 1, pageSize.orElse(10), Sort.by(Sort.Order.asc("beerName")));
        Page<Beer> beerPage = null;
        if (beerName.isPresent() && beerStyle.isEmpty())
            beerPage = beerRepository.findByBeerNameLikeIgnoreCase("%"+beerName.get()+"%", pageRequest);

        if (beerName.isEmpty() && beerStyle.isPresent())
            beerPage = beerRepository.findByBeerStyle(beerStyle.get(), pageRequest);

        if (beerName.isPresent() && beerStyle.isPresent())
            beerPage = beerRepository.findByBeerNameLikeIgnoreCaseAndBeerStyle("%"+beerName.get()+"%", beerStyle.get(), pageRequest);

        if (beerName.isEmpty() && beerStyle.isEmpty())
            beerPage = beerRepository.findAll(pageRequest);

        return beerPage.map(beerMapper::beerToBeerDTO);
    }

    @Override
    public List<BeerDTO> searchBeers(BeerSearchCriteria criteria) {
        log.info("Search beer by criteria - in service: {}", criteria);
        return beerRepository.findBySearchCriteria(criteria).stream().map(beerMapper::beerToBeerDTO).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "beerCache", key = "#id")
    @Override
    public Optional<BeerDTO> getById(UUID id) {
        log.info("Get Beer by Id - in service. Id: {}", id.toString());
        return beerRepository.findById(id).map(beerMapper::beerToBeerDTO);
    }

    @Override
    public BeerDTO save(BeerDTO beerDTO) {
        log.info("Saving beer to service. Id: {}", beerDTO);
        clearBeerListCache();
        Beer savedBeer = beerRepository.save(beerMapper.beertDTOtoBeer(beerDTO));
        return beerMapper.beerToBeerDTO(savedBeer);
    }

    //Cache Eviction does not work because it is in the same class as Get and Get List
    /*@Caching(evict = {
            @CacheEvict(cacheNames = "beerCache", key = "#beerId"),
            @CacheEvict(cacheNames = "beerListCache")
    })*/
    @Override
    public Optional<BeerDTO> update(UUID beerId, BeerDTO beer) {
        log.info("Update Beer - in service. Id: {}", beerId);
        evictCache(beerId);


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

    private void evictCache(UUID beerId) {
        log.info("Evicting cache for beerId: {}", beerId.toString());
        if (cacheManager.getCache("beerCache") != null)
            Objects.requireNonNull(cacheManager.getCache("beerCache")).evict(beerId);
        clearBeerListCache();
    }

    private void clearBeerListCache() {
        if (cacheManager.getCache("beerListCache")!= null)
            Objects.requireNonNull(cacheManager.getCache("beerListCache")).clear();
    }

    @Override
    public Boolean deleteById(UUID beerId) {
        log.info("Delete Beer - in service. Id: {}", beerId.toString());
        evictCache(beerId);
       if(beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;
    }

    @Override
    public void patchById(UUID beerId, BeerDTO beer) {
        log.debug("Patch Beer - in service. Id: {}", beerId.toString());
        evictCache(beerId);
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
