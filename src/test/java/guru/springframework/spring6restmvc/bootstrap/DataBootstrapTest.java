package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author john
 * @since 12/07/2024
 */
@SpringBootTest
class DataBootstrapTest {

    @Autowired
    BeerService beerService;
    @Autowired
    CustomerService customerService;
    @Autowired
    DataBootstrap dataBootstrap;

    @BeforeEach
    void setUp() {
        //dataBootstrap = new DataBootstrap(beerService, customerService);
    }

    @Test
    void run() throws Exception {
        //dataBootstrap.run(null);
        assertTrue(beerService.count()>0);
        assertEquals(2, customerService.count());
    }
}