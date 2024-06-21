package ru.checkdev.auth.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.kafka.AuthPersonDto;
import ru.checkdev.auth.service.PersonService;

import java.util.Calendar;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthStreamReceiverImpl {

    private final PersonService persons;
    private final StreamTransmitter streamTransmitter;
    private static final String AUTH_REG_RESPONSE = "auth-2";
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "auth-1", groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    public void receive(ConsumerRecord<String, JsonNode> myRecord) {
        log.info("kafka request auth_reg_request: [{}]", myRecord.value().toString());
        log.info("class: {}", myRecord.value().getClass().getSimpleName());
        AuthPersonDto data = convertJsonNodeToDto(myRecord.value(), AuthPersonDto.class);
        Profile profile = new Profile(data.getEmail(), data.getEmail(), data.getPassword());
        profile.setPrivacy(data.isPrivacy());
        profile.setCreated(Calendar.getInstance());
        Optional<Profile> result = this.persons.reg(profile);

        Object response = result.<Object>map(prs -> new Object() {
            public String getChatId() {
                return data.getChatId();
            }

            public String getEmail() {
                return data.getEmail();
            }

            public String getPassword() {
                return data.getPassword();
            }
        }).orElseGet(() -> new Object() {
            public String getChatId() {
                return data.getChatId();
            }

            public String getError() {
                return String.format("Пользователь с почтой %s уже существует.", profile.getEmail());
            }
        });
        streamTransmitter.send(AUTH_REG_RESPONSE, response);
        log.info(" kafka response auth_reg_request: [{}]", String.valueOf(response));
    }

    private <T> T convertJsonNodeToDto(JsonNode jsonNode, Class<T> dtoClass) {
        try {
            return objectMapper.treeToValue(jsonNode, dtoClass);
        } catch (Exception e) {
            log.error("convertJsonNodeToDto method error: {}", e.getMessage());
            throw new RuntimeException("Failed to convert JSON to DTO", e);
        }
    }
}
