package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvc.services.BeerOrderLineService;
import guru.springframework.spring6restmvcapi.enums.BeerOrderLineStatus;
import guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Author:john
 * Date:08/05/2025
 * Time:02:24
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class DrinkPreparedListener {

    private final BeerOrderLineService beerOrderLineService;

    @KafkaListener(groupId="drinks.prepared.consumer", topics= KafkaConfig.DRINK_PREPARED_TOPIC)
    public void drinkPrepared(DrinkPreparedEvent drinkPreparedEvent) {
        log.info("Drink prepared event received: {}", drinkPreparedEvent);
        drinkPreparedEvent.getBeerOrderLine().setStatus(BeerOrderLineStatus.COMPLETE);
        BeerOrderLineDTO updated = beerOrderLineService.update(drinkPreparedEvent.getBeerOrderLine().getId(), drinkPreparedEvent.getBeerOrderLine());
        log.info("Updated drink order line status: " + updated.getStatus());
    }
}
