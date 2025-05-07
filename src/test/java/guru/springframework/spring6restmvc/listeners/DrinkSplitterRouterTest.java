package guru.springframework.spring6restmvc.listeners;

import guru.springframework.spring6restmvc.config.KafkaConfig;
import guru.springframework.spring6restmvcapi.enums.BeerStyle;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author:john
 * Date:01/05/2025
 * Time:05:20
 */
@SpringBootTest
@EmbeddedKafka(controlledShutdown = true, topics={KafkaConfig.ORDER_PLACED_TOPIC}, partitions = 1, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DrinkSplitterRouterTest {

    @Autowired
    DrinkSplitterRouter drinkSplitterRouter;

    @Autowired
    DrinkRequestKafkaConsumer drinkRequestKafkaConsumer;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    @BeforeEach
    void setUp() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(messageListenerContainer -> {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
        });
    }

    @Test
    void receive() {
        OrderPlacedEvent e = OrderPlacedEvent.builder().beerOrderDTO(buildOrder()).build();
        drinkSplitterRouter.receive(e);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, drinkRequestKafkaConsumer.iceColdMessageCounter.get()));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, drinkRequestKafkaConsumer.coldMessageCounter.get()));
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                assertEquals(1, drinkRequestKafkaConsumer.coolMessageCounter.get()));

    }

    BeerOrderDTO buildOrder() {

        Set<BeerOrderLineDTO> beerOrderLines = new HashSet<>();

        beerOrderLines.add(BeerOrderLineDTO.builder()
                .beer(BeerDTO.builder()
                        .id(UUID.randomUUID())
                        .beerStyle(BeerStyle.IPA)
                        .beerName("Test Beer 1")
                        .build())
                .build());

        //add lager
        beerOrderLines.add(BeerOrderLineDTO.builder()
                .beer(BeerDTO.builder()
                        .id(UUID.randomUUID())
                        .beerStyle(BeerStyle.LAGER)
                        .beerName("Test Beer 2")
                        .build())
                .build());

        //add gose
        beerOrderLines.add(BeerOrderLineDTO.builder()
                .beer(BeerDTO.builder()
                        .id(UUID.randomUUID())
                        .beerStyle(BeerStyle.GOSE)
                        .beerName("Test Beer 3")
                        .build())
                .build());

        return BeerOrderDTO.builder()
                .id(UUID.randomUUID())
                .orderLines(beerOrderLines)
                .build();
    }
}