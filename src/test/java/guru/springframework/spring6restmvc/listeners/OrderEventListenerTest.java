package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import guru.springframework.spring6restmvcapi.model.BeerDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderLineDTO;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author:john
 * Date:28/04/2025
 * Time:00:17
 */
@SpringBootTest
@EmbeddedKafka(controlledShutdown = true, topics = {KafkaConfig.ORDER_PLACED_TOPIC}, partitions = 1, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderEventListenerTest {

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    OrderEventListener orderEventListener;

    @Autowired
    OrderPlacedKafkaListener orderPlacedKafkaListener;

    @BeforeEach
    void setup() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(messageListenerContainer -> {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
        });
    }

    @Test
    void listener() {
        OrderPlacedEvent e = OrderPlacedEvent.builder().beerOrderDTO(BeerOrderDTO.builder().id(UUID.randomUUID())
                .orderLines(Set.of(BeerOrderLineDTO.builder().beer(BeerDTO.builder().beerName("Bla bla").build())
                        .build())).build()).build();
        orderEventListener.listener(e);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, orderPlacedKafkaListener.messageCounter.get()));

    }
}