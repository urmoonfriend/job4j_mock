package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

@Service("/forget")
@RequiredArgsConstructor
@Slf4j
public class PasswordResetAction implements Action {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final TgConfig tgConfig = new TgConfig("tg/", 8);

    @Override
    public BotApiMethod<Message> handle(Message message) {
        return null;
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return null;
    }
}
