package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvcapi.events.DrinkRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:john
 * Date:01/05/2025
 * Time:03:42
 */
@Component
public class DrinkRequestKafkaConsumer {

    AtomicInteger iceColdMessageCounter = new AtomicInteger(0);
    AtomicInteger coldMessageCounter = new AtomicInteger(0);
    AtomicInteger coolMessageCounter = new AtomicInteger(0);

    @KafkaListener(groupId="KafkaIntegrationTest", topics= KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC)
    public void receiveIceCold(DrinkRequestEvent drinkRequestEvent)  {
        System.out.println("Kafka Received Ice-cold Beer Order: "+drinkRequestEvent);
        iceColdMessageCounter.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics= KafkaConfig.DRINK_REQUEST_COLD_TOPIC)
    public void receiveCold(DrinkRequestEvent drinkRequestEvent)  {
        System.out.println("Kafka Received Cold Beer Order: "+drinkRequestEvent);
        coldMessageCounter.incrementAndGet();
    }

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_COOL_TOPIC)
    public void receiveCool(DrinkRequestEvent drinkRequestEvent)  {
        System.out.println("Kafka Received Cool Beer Order: "+drinkRequestEvent);
        coolMessageCounter.incrementAndGet();
    }

}
