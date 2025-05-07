package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.domain.BeerOrder;
import guru.springframework.spring6restmvc.domain.BeerOrderLine;
import guru.springframework.spring6restmvc.domain.BeerOrderShipment;
import guru.springframework.spring6restmvc.exceptions.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerOrderCreateMapper;
import guru.springframework.spring6restmvc.mappers.BeerOrderMapper;
import guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import guru.springframework.spring6restmvcapi.model.BeerOrderCreateDTO;
import guru.springframework.spring6restmvcapi.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.repository.BeerOrderRepository;
import guru.springframework.spring6restmvc.repository.BeerRepository;
import guru.springframework.spring6restmvc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author:john
 * Date:22/02/2025
 * Time:19:09
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderCreateMapper beerOrderCreateMapper;

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    private final ApplicationEventPublisher applicationEventPublisher;


    @Override
    public Page<BeerOrderDTO> listOrders(Integer pageNumber, Integer pageSize) {
        log.info("List BeerOrders - in service");
        return beerOrderRepository.findAll(PageRequest.of(pageNumber!=null?(pageNumber-1):0, pageSize!=null?pageSize:10))
                .map(beerOrderMapper::beerOrderToBeerOrderDTO);
    }

    @Transactional
    @Override
    public BeerOrderDTO createOrder(BeerOrderDTO beerOrderDTO) {
        log.info("Create BeerOrder {} - in service", beerOrderDTO);
        return beerOrderMapper.beerOrderToBeerOrderDTO(
                beerOrderRepository.save(beerOrderMapper.beerOrderDTOToBeerOrder(beerOrderDTO)));
    }

    @Transactional
    @Override
    public BeerOrderDTO createOrder(BeerOrderCreateDTO beerOrderCreateDTO) {
        log.info("Create Order - In Service from Create DTO {}", beerOrderCreateDTO);
        return beerOrderMapper.beerOrderToBeerOrderDTO(
                beerOrderRepository.save(beerOrderCreateMapper.beerOrderCreateDTOToBeerOrder(beerOrderCreateDTO)));
    }

    @Override
    public Optional<BeerOrderDTO> getOrderById(UUID orderId) {
        log.info("Get Order By Id - In Service");
        return beerOrderRepository.findById(orderId).map(beerOrderMapper::beerOrderToBeerOrderDTO);
    }

    @Override
    public Optional<BeerOrderDTO> updateOrder(UUID orderId, BeerOrderDTO beerOrderDTO) {
        log.info("Update Order - In Service");
        BeerOrder beerOrder = beerOrderMapper.beerOrderDTOToBeerOrder(beerOrderDTO);
        AtomicReference<Optional<BeerOrderDTO>> updatedOrderDTO = new AtomicReference<>();
        beerOrderRepository.findById(orderId)
                .ifPresentOrElse(foundOrder -> {
                            foundOrder.setBeerOrderShipment(beerOrder.getBeerOrderShipment());
                    foundOrder.setOrderLines(beerOrder.getOrderLines());
                    foundOrder.setCustomer(beerOrder.getCustomer());
                    updatedOrderDTO.set(Optional.of(beerOrderMapper.beerOrderToBeerOrderDTO(beerOrderRepository.save(foundOrder))));
                },
                        ()->  updatedOrderDTO.set(Optional.empty()));

        if (beerOrderDTO.getPaymentAmount()!=null && updatedOrderDTO.get().isPresent()) {
            applicationEventPublisher.publishEvent(OrderPlacedEvent.builder().beerOrderDTO(updatedOrderDTO.get().get()).build());
        }
        return updatedOrderDTO.get();
    }

    @Override
    public BeerOrderDTO updateBeerOrder(UUID orderId, BeerOrderDTO beerOrderDTO) {
        log.info("Update Beer Order");
        BeerOrder beerOrder = beerOrderRepository.findById(orderId).orElseThrow(NotFoundException::new);
        beerOrderDTO.getOrderLines().forEach(orderLineDTO -> {
            if (orderLineDTO.getId()!=null) {
                BeerOrderLine beerOrderLine = beerOrder.getOrderLines().stream().filter(line -> line.getId().equals(orderLineDTO.getId()))
                        .findFirst().orElseThrow(NotFoundException::new);
                beerOrderLine.setBeer(beerRepository.findById(orderLineDTO.getBeer().getId()).orElseThrow(NotFoundException::new));
                beerOrderLine.setOrderQuantity(orderLineDTO.getOrderQuantity());
            } else {
                beerOrder.getOrderLines().add(BeerOrderLine.builder()
                        .beer(beerRepository.findById(orderLineDTO.getBeer().getId()).orElseThrow(NotFoundException::new))
                        .orderQuantity(orderLineDTO.getOrderQuantity())
                        .beerOrder(beerOrder).build());
            }

        });

        if(beerOrderDTO.getBeerOrderShipment()!=null) {
            beerOrder.setBeerOrderShipment(BeerOrderShipment.builder().id(beerOrderDTO.getBeerOrderShipment().getId())
                    .trackingNumber(beerOrderDTO.getBeerOrderShipment().getTrackingNumber()).build());
        }
        if(beerOrderDTO.getCustomer()!=null && beerOrderDTO.getCustomer().getId()!=null) {
            beerOrder.setCustomer(customerRepository.findById(beerOrderDTO.getCustomer().getId()).orElseThrow(NotFoundException::new));
        }
        return beerOrderMapper.beerOrderToBeerOrderDTO(beerOrderRepository.save(beerOrder));
    }

    @Override
    public Boolean deleteOrder(UUID orderId) {
        log.info("Delete Order - In Service");
        if(beerOrderRepository.existsById(orderId)) {
            beerOrderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }

    @Override
    public void patchOrder(UUID orderId, BeerOrderDTO beerOrderDTO) {
        log.info("Patch Order - In Service");
        BeerOrder beerOrder = beerOrderMapper.beerOrderDTOToBeerOrder(beerOrderDTO);
        beerOrderRepository.findById(orderId)
               .ifPresentOrElse(foundOrder -> {
                    if (beerOrder.getBeerOrderShipment()!= null) {
                        foundOrder.setBeerOrderShipment(beerOrder.getBeerOrderShipment());
                    }
                    if (beerOrder.getOrderLines()!= null) {
                        foundOrder.setOrderLines(beerOrder.getOrderLines());
                    }
                    if (beerOrder.getCustomer()!= null) {
                        foundOrder.setCustomer(beerOrder.getCustomer());
                    }
                    beerOrderRepository.save(foundOrder);
                }, ()-> {
                   throw new NotFoundException();
               });
    }

    @Override
    public long count() {
        return beerOrderRepository.count();
    }
}
