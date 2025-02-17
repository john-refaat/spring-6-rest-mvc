package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.events.BeerCreatedEvent;
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
@Component
public class BeerCreatedListener {

    @Async
    @EventListener
    public void listener(BeerCreatedEvent event) {
        Beer beer = event.getBeer();
        Authentication authentication = event.getAuthentication();

        log.info("New beer created: {} by {}", beer.getBeerName(), authentication.getName());
        log.info("Thread ID: {}", Thread.currentThread().getId());
        log.info("Thread Name: {}", Thread.currentThread().getName());
        // Implement additional logic here, e.g., sending email or triggering a notification service.
        // The event object contains the beer and authentication context.
    }

}
