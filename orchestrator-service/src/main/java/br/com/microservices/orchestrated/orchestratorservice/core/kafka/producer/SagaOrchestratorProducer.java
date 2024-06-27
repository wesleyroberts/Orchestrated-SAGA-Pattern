package br.com.microservices.orchestrated.orchestratorservice.core.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String payload, String topic){
        try {
            log.info("Send event to topic {} with data {}",topic,payload);
            kafkaTemplate.send(topic,payload);
        }catch (Exception e){
            log.error("Error trying to send data to topic {} with data {}", topic,payload);
        }
    }

}
