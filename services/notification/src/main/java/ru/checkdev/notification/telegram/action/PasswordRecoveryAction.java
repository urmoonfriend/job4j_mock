package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ProfileDto;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.client.TgAuthCallWebClint;

@Service("/forget")
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String OK = "ok";
    private static final String URL_FORGET = "/forgot";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email для сброса пароля:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var email = message.getText();
        var text = "";
        var sl = System.lineSeparator();
        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " не корректный." + sl
                    + "попробуйте снова." + sl
                    + "/new";
        } else {
            text = getTextFromAuth(email, sl);
        }
        return new SendMessage(chatId, text);
    }

    private String getTextFromAuth(String email, String sl) {
        String text = "";
        try {
            Object result = authCallWebClint.doPost(URL_FORGET, new ProfileDto().setEmail(email)).block();
            var mapObject = tgConfig.getObjectToMap(result);
            if (mapObject.containsKey(ERROR_OBJECT)) {
                text = "Ошибка смены пароля: " + mapObject.get(ERROR_OBJECT);
            } else {
                String password = mapObject.get(OK);
                text = String.format("Новый пароль: %s", password) + sl;
            }
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
        }
        return text;
    }
}
