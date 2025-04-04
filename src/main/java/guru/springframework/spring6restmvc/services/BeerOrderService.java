package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:07
 */
public interface BeerOrderService {

    Page<BeerOrderDTO> listOrders(Integer pageNumber, Integer pageSize);
    BeerOrderDTO createOrder(BeerOrderCreateDTO beerOrderCreateDTO);
    BeerOrderDTO createOrder(BeerOrderDTO beerOrderDTO);
    Optional<BeerOrderDTO> getOrderById(UUID orderId);
    Optional<BeerOrderDTO> updateOrder(UUID orderId, BeerOrderDTO beerOrderDTO);
    BeerOrderDTO updateBeerOrder(UUID orderId, BeerOrderDTO beerOrderDTO);
    Boolean deleteOrder(UUID orderId);
    void patchOrder(UUID orderId, BeerOrderDTO beerOrderDTO);

    long count();
}