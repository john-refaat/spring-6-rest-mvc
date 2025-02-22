package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerAudit;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
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
 * Date:16/02/2025
 * Time:05:06
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerCreatedEventListener {

    private final BeerMapper beerMapper;
    private final BeerAuditRepository beerAuditRepository;

    @Async
    @EventListener
    public void listener(BeerCreatedEvent event) {
        Beer beer = event.getBeer();
        Authentication authentication = event.getAuthentication();

        log.info("New beer created: {} by {}", beer.getBeerName(), authentication);

        BeerAudit beerAudit = beerMapper.beerToBeerAudit(event.getBeer());

        beerAudit.setAuditEventType("BEER_CREATED");
        if(event.getAuthentication() != null && event.getAuthentication().getName() != null)
            beerAudit.setPrincipalName(authentication.getName());
        log.info("Beer Audit: {}", beerAudit);

        beerAuditRepository.save(beerAudit);
    }

}
