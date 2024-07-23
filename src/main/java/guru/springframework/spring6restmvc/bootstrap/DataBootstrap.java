package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.BeerCSVService;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.CustomerService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private final BeerCSVService beerCSVService;

    public DataBootstrap(BeerService beerService, CustomerService customerService, BeerCSVService beerCSVService) {
        this.beerService = beerService;
        this.customerService = customerService;
        this.beerCSVService = beerCSVService;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting DataBootstrap");
        loadBeers();
        loadCSVBeers();
        loadCustomers();
    }

    private void loadCSVBeers() throws FileNotFoundException {
        if (beerService.count() < 10) {
            File csvFile = ResourceUtils.getFile("classpath:csvdata/beers.csv");
            List<BeerCSVRecord> records = beerCSVService.convertCSV(csvFile);
            for (BeerCSVRecord rec : records) {
                beerService.save(BeerDTO.builder().beerName(StringUtils.abbreviate(rec.getBeer(), 50)).price(BigDecimal.TEN).quantityOnHand(rec.getCountX())
                        .beerStyle(getBeerStyle(rec)).upc(String.valueOf(rec.getRow())).build());
            }
        }
    }

    private BeerStyle getBeerStyle(BeerCSVRecord beerCSVRecord) {
        return switch (beerCSVRecord.getStyle()) {
            case "American Pale Lager" -> BeerStyle.LAGER;
            case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                    BeerStyle.ALE;
            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
            case "American Porter" -> BeerStyle.PORTER;
            case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
            case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
            case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
            case "English Pale Ale" -> BeerStyle.PALE_ALE;
            default -> BeerStyle.PILSNER;
        };
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
