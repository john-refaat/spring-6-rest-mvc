package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerSearchCriteria;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author john
 * @since 01/07/2024
 */
@Slf4j
@RestController
@RequestMapping(BeerController.PATH)
public class BeerController {
    public static final String PATH = "/api/v1/beer";
    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping({"/", ""})
    public List<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                   @RequestParam(required = false)BeerStyle beerStyle) {
        log.debug("Search beers - in controller");
        return beerService.listBeers(Optional.ofNullable(beerName), Optional.ofNullable(beerStyle));
    }

    @PostMapping({"/search"})
    public List<BeerDTO> saerchBeers(@RequestBody BeerSearchCriteria criteria) {
        log.info("Search beers - in controller");
        return beerService.searchBeers(criteria);
    }

    @GetMapping("/{beerId}")
    public BeerDTO getById(@PathVariable UUID beerId) {
        log.debug("Get Beer by Id - in controller. Id: {}", beerId.toString());
        return beerService.getById(beerId).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"/", ""})
    public BeerDTO save(@Validated @RequestBody BeerDTO beer, HttpServletResponse response) {
        log.debug("Save Beer - in controller");
        BeerDTO savedBeer = beerService.save(beer);
        response.addHeader("Location", PATH+"/"+savedBeer.getId().toString());
        return savedBeer;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{beerId}")
    public BeerDTO update(@Validated @PathVariable UUID beerId, @Validated @RequestBody BeerDTO beer) {
        log.debug("Update Beer - in controller. Id: {}", beerId.toString());
        return beerService.update(beerId, beer).orElseThrow(NotFoundException::new);
    }

   @ResponseStatus(HttpStatus.NO_CONTENT)
   @PatchMapping("/{beerId}")
   public void patchBeerById(@PathVariable UUID beerId, @RequestBody BeerDTO beer) {
       log.debug("Patch Beer - in controller. Id: {}", beerId.toString());
        beerService.patchById(beerId, beer);
   }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{beerId}")
    public void deleteCustomer(@PathVariable UUID beerId) {
        log.debug("Delete Beer - in controller. Id: {}", beerId.toString());
        Boolean deleted = beerService.deleteById(beerId);
        if (!deleted) {
            throw new NotFoundException("Beer not found with id: " + beerId);
        }
    }

}
