package ru.checkdev.notification.streams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamTransmitter {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(KafkaMessage dto) {
        log.info("Send stream message: {}", dto);
        kafkaTemplate.send(dto.getQueue(), dto.getPayload());
    }
}
