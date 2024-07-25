package br.com.microservices.orchestrated.orderservice.core.service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.core.document.Event;
import br.com.microservices.orchestrated.orderservice.core.dto.EventFilters;
import br.com.microservices.orchestrated.orderservice.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;

    public void notifyEnding(Event event){
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId: {}",event.getOrderId(),event.getTransactionId());
    }

    public List<Event> findAll(){
       return repository.findAll();
    }

    public Event findByFilters(EventFilters filters){
        validationEmptyFilters(filters);
        if( !ObjectUtils.isEmpty(filters.getOrderId())){
            return findByOrderId(filters.getOrderId());
        }else {
            return findByTransactionId(filters.getTransactionId());
        }
    }

    private Event findByTransactionId(String transactionId){
        return repository
                .findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(()-> new ValidationException("Event not found by TransactionID."));
    }

    private Event findByOrderId(String orderId){
        return repository
                .findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(()-> new ValidationException("Event not found by orderID."));
    }

    private void validationEmptyFilters(EventFilters filters){
        if(ObjectUtils.isEmpty(filters.getOrderId()) && ObjectUtils.isEmpty(filters.getTransactionId())){
           throw new ValidationException("OrderID or TransactionsID must be Informed.");
        }
    }

    public Event save(Event event){
        return repository.save(event);
    }
}
