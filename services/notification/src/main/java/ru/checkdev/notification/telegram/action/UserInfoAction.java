package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.client.TgAuthCallWebClint;

@Service("/check")
@RequiredArgsConstructor
@Slf4j
public class UserInfoAction implements Action {
    private static final String ERROR_OBJECT = "error";
    private static final String USERNAME_OBJECT = "username";
    private static final String EMAIL_OBJECT = "email";
    private static final String URL_AUTH_PERSON_INFO = "/person/by/email";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    @Value("${server.site.url.login}")
    private String urlSiteAuth;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email для выдачи информации:";
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
            Object result = authCallWebClint.doGet(String.format("%s?email=%s", URL_AUTH_PERSON_INFO, email)).block();
            var mapObject = tgConfig.getObjectToMap(result);
            log.info("result: [{}]", result);
            if (mapObject.containsKey(ERROR_OBJECT)) {
                text = "Ошибка получения данных: " + mapObject.get(ERROR_OBJECT);
            } else {
                text = "ФИО: " + mapObject.get(USERNAME_OBJECT) + sl
                        + "Почта: " + mapObject.get(EMAIL_OBJECT) + sl;
            }
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
        }
        return text;
    }
}
