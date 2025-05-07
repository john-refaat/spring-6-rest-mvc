package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:john
 * Date:28/04/2025
 * Time:00:26
 */
@Component
public class OrderPlacedKafkaListener {
    AtomicInteger messageCounter = new AtomicInteger();

    @KafkaListener(groupId="KafkaIntegrationTest", topics= KafkaConfig.ORDER_PLACED_TOPIC)
    public void receive(OrderPlacedEvent orderPlacedEvent)  {
        System.out.println("Kafka Received: "+orderPlacedEvent);
        messageCounter.incrementAndGet();
    }
}
