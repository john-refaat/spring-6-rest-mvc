package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.domain.BeerAudit;
import guru.springframework.spring6restmvc.events.BeerPatchEvent;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repository.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Author:john
 * Date:20/02/2025
 * Time:04:58
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerPatchedEventListener {
    private final BeerMapper beerMapper;
    private final BeerAuditRepository beerAuditRepository;

    @Async
    @EventListener
    public void listener(BeerPatchEvent event) {
        Beer beer = event.getBeer();
        log.info("Beer patched Event: {} by {}", beer.getBeerName(), event.getAuthentication());

        BeerAudit beerAudit = beerMapper.beerToBeerAudit(beer);
        beerAudit.setAuditEventType("BEER_PATCHED");
        if(event.getAuthentication() != null && event.getAuthentication().getName() != null)
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        log.info("Beer Audit: {}", beerAudit);
        beerAuditRepository.save(beerAudit);
    }
}
