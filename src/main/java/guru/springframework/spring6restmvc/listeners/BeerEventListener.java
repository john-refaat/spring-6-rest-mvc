package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerAudit;
import guru.springframework.spring6restmvc.events.*;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repository.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Author:john
 * Date:22/02/2025
 * Time:04:11
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerEventListener {

    private final BeerMapper beerMapper;
    private final BeerAuditRepository beerAuditRepository;


    @Async
    @EventListener
    void listener(BeerEvent event) {
        Beer beer = event.getBeer();
        Authentication authentication = event.getAuthentication();

        log.info("New beer Event: {} by {}", beer.getId(), authentication);

        BeerAudit beerAudit = beerMapper.beerToBeerAudit(event.getBeer());

        beerAudit.setAuditEventType(getBeerEventType(event));
        if(event.getAuthentication() != null && event.getAuthentication().getName() != null)
            beerAudit.setPrincipalName(authentication.getName());
        log.info("Beer Audit: {}", beerAudit);

        beerAuditRepository.save(beerAudit);
    }

    private String getBeerEventType(BeerEvent event) {
        String eventType;
        switch (event) {
            case BeerCreatedEvent beerCreatedEvent-> eventType =  "BEER_CREATED";
            case BeerUpdatedEvent beerUpdatedEvent-> eventType = "BEER_UPDATED";
            case BeerPatchEvent beerPatchedEvent-> eventType = "BEER_PATCHED";
            case BeerDeletedEvent beerDeletedEvent-> eventType = "BEER_DELETED";
            default -> eventType = "UNKNOWN";
        }
        return eventType;
    }
}
