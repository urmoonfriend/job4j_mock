package ru.checkdev.notification.streams.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.telegram.BotMenu;
import ru.checkdev.notification.telegram.config.TgConfig;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthStreamReceiverImpl {

    private final BotMenu botMenu;
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private static final String ERROR_OBJECT = "error";
    private static final String CHAT_ID = "chatId";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String SL = System.lineSeparator();

    @Value("${server.site.url.login}")
    private String urlSiteAuth;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "auth-2", groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    public Object receive(ConsumerRecord<Integer, JsonNode> myRecord) {
        log.debug("kafka request auth_reg_response: [{}]", myRecord.value().toString());
        Object data = convertJsonNodeToDto(myRecord.value(), Object.class);
        handle(data);
        return data;
    }

    private void handle(Object data) {
        var mapObject = tgConfig.getObjectToMap(data);
        if (mapObject.containsKey(CHAT_ID)) {
            String text = "Сервис не доступен попробуйте позже" + SL + "/start";
            if (mapObject.containsKey(EMAIL) && mapObject.containsKey(PASSWORD)) {
                String email = mapObject.get(EMAIL);
                String password = mapObject.get(PASSWORD);
                text = "Вы зарегистрированы: " + SL
                        + "Логин: " + email + SL
                        + "Пароль: " + password + SL
                        + urlSiteAuth;
            } else if (mapObject.containsKey(ERROR_OBJECT)) {
                text = "Ошибка регистрации: " + mapObject.get(ERROR_OBJECT);
            }
            log.info("kafka response auth_reg_response: [{}]", text);
            botMenu.send(mapObject.get(CHAT_ID), text);
        }
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
