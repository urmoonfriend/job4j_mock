package ru.checkdev.auth.streams;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StreamTransmitter {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String queue, Object dto) {
        kafkaTemplate.send(queue, dto);
    }
}

