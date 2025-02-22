package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.BeerAudit;
import guru.springframework.spring6restmvc.events.BeerDeletedEvent;
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
 * Time:05:41
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BeerDeletedEventListener {

    private final BeerMapper beerMapper;
    private final BeerAuditRepository beerAuditRepository;

    @Async
    @EventListener
    public void listener(BeerDeletedEvent event) {
        log.info("Beer deleted Event: {} by {}", event.getBeer().getBeerName(), event.getAuthentication());

        BeerAudit beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType("BEER_DELETED");
        if(event.getAuthentication() != null && event.getAuthentication().getName() != null)
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        log.info("Beer Audit: {}", beerAudit);
        beerAuditRepository.save(beerAudit);
    }

}
