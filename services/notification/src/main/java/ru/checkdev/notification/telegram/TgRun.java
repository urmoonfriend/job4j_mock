package ru.checkdev.notification.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.checkdev.notification.telegram.action.*;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.List;
import java.util.Map;

/**
 * 3. Мидл
 * Инициализация телеграм бот,
 * username = берем из properties
 * token = берем из properties
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TgRun {
    private final TgAuthCallWebClint tgAuthCallWebClint;
    @Value("${tg.username}")
    private String username;
    @Value("${tg.token}")
    private String token;
    private final Map<String, Action> actionMap;

    @Bean
    public void initTg() {
        try {
            BotMenu menu = new BotMenu(actionMap, username, token);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(menu);
        } catch (TelegramApiException e) {
            log.error("Telegram bot: {}, ERROR {}", username, e.getMessage());
        }
    }
}
