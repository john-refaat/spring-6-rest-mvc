package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
import guru.springframework.spring6restmvc.events.BeerDeletedEvent;
import guru.springframework.spring6restmvc.events.BeerPatchEvent;
import guru.springframework.spring6restmvc.events.BeerUpdatedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher applicationEventPublisher;


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

    @Transactional
    @Override
    public BeerDTO save(BeerDTO beerDTO) {
        log.info("Saving beer in service {}", beerDTO);
        clearBeerListCache();
        Beer savedBeer = beerRepository.save(beerMapper.beertDTOtoBeer(beerDTO));
        beerRepository.flush();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        applicationEventPublisher.publishEvent(new BeerCreatedEvent(savedBeer, authentication));
        return beerMapper.beerToBeerDTO(savedBeer);
    }

    //Cache Eviction does not work because it is in the same class as Get and Get List
    /*@Caching(evict = {
            @CacheEvict(cacheNames = "beerCache", key = "#beerId"),
            @CacheEvict(cacheNames = "beerListCache")
    })*/

    @Transactional
    @Override
    public Optional<BeerDTO> update(UUID beerId, BeerDTO beer) {
        log.info("Update Beer - in service. Id: {}", beerId);
        evictCache(beerId);


        AtomicReference<Beer> atomicReference = new AtomicReference<>();
        beerRepository.findById(beerId).ifPresentOrElse(beerFound -> {
            beerFound.setBeerName(beer.getBeerName());
            beerFound.setBeerStyle(beer.getBeerStyle());
            beerFound.setUpc(beer.getUpc());
            beerFound.setPrice(beer.getPrice());
            beerFound.setQuantityOnHand(beer.getQuantityOnHand());

            Beer updatedBeer = beerRepository.save(beerFound);
            beerRepository.flush();
            atomicReference.set(updatedBeer);
        }, () -> atomicReference.set(null));
        Optional<BeerDTO> beerDTOOptional = Optional.empty();
        if (atomicReference.get() != null) {
            Beer updated= atomicReference.get();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            applicationEventPublisher.publishEvent(new BeerUpdatedEvent(updated, authentication));
            beerDTOOptional = Optional.of(beerMapper.beerToBeerDTO(updated));
        }
        return beerDTOOptional;
    }

    @Transactional
    @Override
    public Boolean deleteById(UUID beerId) {
        log.info("Delete Beer - in service. Id: {}", beerId.toString());
        evictCache(beerId);

       if(beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           applicationEventPublisher.publishEvent(BeerDeletedEvent.builder().authentication(authentication)
                   .beer(Beer.builder().id(beerId).build()).build());
            return true;
        }
        return false;
    }

    @Transactional
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

        Beer savedBeer = beerRepository.save(existingBeer);
        beerRepository.flush();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        applicationEventPublisher.publishEvent(BeerPatchEvent.builder().beer(savedBeer).authentication(authentication).build());
    }

    @Override
    public long count() {
        return beerRepository.count();
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

}
