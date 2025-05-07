package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvcapi.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.services.BeerOrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Author:john
 * Date:26/02/2025
 * Time:04:45
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(BeerOrderController.PATH)
public class BeerOrderController {
    public static final String PATH = "/api/v1/beer_order";
    private final BeerOrderService beerOrderService;

    @GetMapping({"/", ""})
    public Map<String, Object> listBeerOrders(@RequestParam(required = false) Integer pageNumber,
                                              @RequestParam(required = false) Integer pageSize) {
        log.info("List beer orders - in controller");
        Page<BeerOrderDTO> page = beerOrderService.listOrders(pageNumber, pageSize);
        return Map.of("content", page.getContent(),
                "pageNumber", page.getNumber(), "pageSize", page.getSize(), "sort", page.getSort().isSorted(),
                "first", page.isFirst(), "last", page.isLast(), "totalPages", page.getTotalPages(), "totalElements", page.getTotalElements());
    }

    @GetMapping("/{orderId}")
    public BeerOrderDTO getBeerOrderById(@PathVariable UUID orderId) {
        log.info("Get beer order by id - in controller");
        return beerOrderService.getOrderById(orderId).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping({"/", ""})
    public BeerOrderDTO createBeerOrder(@Validated @RequestBody BeerOrderCreateDTO beerOrderDTO, HttpServletResponse response) {
        log.info("Create beer order - in controller");
        BeerOrderDTO order = beerOrderService.createOrder(beerOrderDTO);
        response.addHeader("Location", PATH + "/" + order.getId().toString());
        log.info("Created {}", order);
        return order;
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{orderId}")
    public BeerOrderDTO updateBeerOrder(@PathVariable UUID orderId, @Validated @RequestBody BeerOrderDTO beerOrderDTO) {
        log.info("Update beer order - in controller");
        return beerOrderService.updateOrder(orderId, beerOrderDTO).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/2/{orderId}")
    public BeerOrderDTO updateBeerOrder2(@PathVariable UUID orderId, @Validated @RequestBody BeerOrderDTO beerOrderDTO) {
        log.info("Update beer order 2 - in controller");
        return beerOrderService.updateBeerOrder(orderId, beerOrderDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{orderId}")
    public void patchBeerOrder(@PathVariable UUID orderId, @RequestBody BeerOrderDTO beerOrderDTO) {
        log.info("Patch beer order - in controller");
        beerOrderService.patchOrder(orderId, beerOrderDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void deleteBeerOrder(@PathVariable UUID orderId) {
        log.info("Delete beer order - in controller");
        Boolean deleted = beerOrderService.deleteOrder(orderId);
        if (!deleted) {
            throw new NotFoundException("Beer order not found with id: " + orderId);
        }
    }

}

