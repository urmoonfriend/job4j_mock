package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.kafka.AuthPersonDto;
import ru.checkdev.notification.domain.kafka.KafkaMessageDto;
import ru.checkdev.notification.streams.StreamTransmitter;
import ru.checkdev.notification.telegram.config.TgConfig;

import java.util.Optional;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Service("/new")
@RequiredArgsConstructor
@Slf4j
public class RegAction implements Action {
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final StreamTransmitter streamTransmitter;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email для регистрации:";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public Optional<BotApiMethod<Message>> callback(Message message) {
        var chatId = message.getChatId().toString();
        var email = message.getText();
        var text = "";
        var sl = System.lineSeparator();

        if (!tgConfig.isEmail(email)) {
            text = "Email: " + email + " не корректный." + sl
                    + "попробуйте снова." + sl
                    + "/new";
            return Optional.of(new SendMessage(chatId, text));
        }
        var password = tgConfig.getPassword();
        try {
            streamTransmitter.send(new KafkaMessageDto<>(
                    "auth-1",
                    new AuthPersonDto()
                    .setEmail(email)
                    .setPassword(password)
                    .setPrivacy(true)
                    .setRoles(null)
                    .setChatId(chatId)));
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl
                    + "/start";
            return Optional.of(new SendMessage(chatId, text));
        }
        return Optional.empty();
    }
}
