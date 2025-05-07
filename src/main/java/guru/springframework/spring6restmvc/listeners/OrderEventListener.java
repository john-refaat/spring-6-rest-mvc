package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Author:john
 * Date:27/04/2025
 * Time:06:34
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderEventListener {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Async
    @EventListener
    public void listener(OrderPlacedEvent event) {
        log.info("New order placed event: {}", event.getBeerOrderDTO());
        kafkaTemplate.send(KafkaConfig.ORDER_PLACED_TOPIC, event);
    }
}
