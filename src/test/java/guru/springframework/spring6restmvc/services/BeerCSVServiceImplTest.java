package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 23/07/2024
 */
class BeerCSVServiceImplTest {

    private BeerCSVServiceImpl beerCSVService;

    @BeforeEach
    void setUp() {
        beerCSVService = new BeerCSVServiceImpl();
    }

    @Test
    void convertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        assertNotNull(file);

        List<BeerCSVRecord> records = beerCSVService.convertCSV(file);
        assertNotNull(records);
        assertThat(records.size()).isGreaterThan(0);
    }
}