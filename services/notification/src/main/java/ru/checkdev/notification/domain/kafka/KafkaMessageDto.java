package ru.checkdev.notification.domain.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.checkdev.notification.streams.KafkaMessage;

@Data
@Accessors(chain = true)
public class KafkaMessageDto<T> implements KafkaMessage {
    private String queue;
    private T payload;

    @JsonCreator
    public KafkaMessageDto(@JsonProperty("queue") String queue, @JsonProperty("payload") T payload) {
        this.queue = queue;
        this.payload = payload;
    }
}
