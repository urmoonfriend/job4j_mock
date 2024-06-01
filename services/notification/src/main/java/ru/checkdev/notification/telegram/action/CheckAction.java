package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;


@Service("/check")
@RequiredArgsConstructor
@Slf4j
public class CheckAction implements Action {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private static final String URL_GET_PROFILE = "/person/check";
    private static final String ERROR_OBJECT = "error";

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email для получения информации:";
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
            return new SendMessage(chatId, text);
        }
        Object result;
        try {
            result = tgAuthCallWebClint.doPost(URL_GET_PROFILE, new PersonDTO()).block();
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Ошибка при получении данных: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        text = "Пользователь: " + sl
                + "Email: " + email + sl
                + "ФИО: " + mapObject.get("username");
        return new SendMessage(chatId, text);
    }
}
