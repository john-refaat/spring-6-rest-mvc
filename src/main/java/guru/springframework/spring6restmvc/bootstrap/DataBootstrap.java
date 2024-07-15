package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author john
 * @since 11/07/2024
 */
@Slf4j
@Component
public class DataBootstrap implements CommandLineRunner {
    private final BeerService beerService;
    private final CustomerService customerService;

    public DataBootstrap(BeerService beerService, CustomerService customerService) {
        this.beerService = beerService;
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting DataBootstrap");
        loadBeers();
        loadCustomers();
    }

    private void loadCustomers() {
        if (customerService.count() > 0){
            // Already loaded, no need to load again
            return;
        }
        CustomerDTO customer1 = CustomerDTO.builder().id(UUID.randomUUID())
                .name("John")
                .version(123)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now()).build();
        CustomerDTO customer2 = CustomerDTO.builder().id(UUID.randomUUID())
                .name("Jane")
                .version(456)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now()).build();
        customerService.save(customer1);
        customerService.save(customer2);
        log.info("Customers loaded: {}", customerService.listCustomers().size());
    }

    private void loadBeers() {
        if (beerService.count() > 0){
            // Already loaded, no need to load again
            return;
        }
        BeerDTO beer1 = BeerDTO.builder()
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(BigDecimal.valueOf(12.5))
                .quantityOnHand(122)
                .build();

        BeerDTO beer2 = BeerDTO.builder()
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(BigDecimal.valueOf(11.80))
                .quantityOnHand(392)
                .build();
        BeerDTO beer3 = BeerDTO.builder()
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(BigDecimal.valueOf(8.5))
                .quantityOnHand(144)
                .build();

        beerService.save(beer1);
        beerService.save(beer2);
        beerService.save(beer3);
        log.info("Beers loaded: {}", beerService.listBeers().size());
    }
}
