package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.ProfileDto;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.service.CategoryService;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.client.TgAuthCallWebClint;

@Service("/subscribe")
@RequiredArgsConstructor
@Slf4j
public class SubscribeAction implements Action {
    private static final String ID = "id";
    private final TgAuthCallWebClint authCallWebClint;
    private static final String URL_AUTH_PERSON_EXISTS = "/person/exists";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final CategoryService categoryService;
    private final SubscribeCategoryService subscribeCategoryService;
    private static final String SL = System.lineSeparator();
    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Для подписки введите данные в формате " + SL
                + "category_id:email:password" + SL + SL
                + "Список доступных категорий: " + SL + SL + getTextFromCategories();
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var requestText = message.getText();
        var text = "";
        text = checkAndGetTextFromAuth(requestText);
        return new SendMessage(chatId, text);
    }

    private String checkAndGetTextFromAuth(String requestText) {
        String text = "Нужно передать параметры в формате category_id:email:password";
        String[] parts = requestText.split(":");
        if (parts.length == 3) {
            if (!tgConfig.isEmail(parts[1])) {
                text = "Email: " + parts[1] + " некорректный." + SL
                        + "попробуйте снова." + SL
                        + "/new";
            } else {
                text = getTextFromAuth(parts);
            }
        }
        return text;
    }

    private String getTextFromAuth(String[] parts) {
        String text = "Сервис не доступен попробуйте позже" + SL + "/start";
        try {
            var profileDto = new ProfileDto().setEmail(parts[1]).setPassword(parts[2]);
            Object result = authCallWebClint.doPost(URL_AUTH_PERSON_EXISTS, profileDto).block();
            var mapObject = tgConfig.getObjectToMap(result);
            if (categoryService.categoryExists(parts[0])) {
                if (mapObject.containsKey(ID)) {
                    Object stringUserId = mapObject.get(ID);
                    Integer categoryId = Integer.valueOf(parts[0]);
                    Integer userId = Integer.valueOf(stringUserId.toString());
                    log.info("categoryId: {}, stringUserId: {}", categoryId, stringUserId);
                    var subscribeCategory = new SubscribeCategory()
                            .setCategoryId(categoryId)
                            .setUserId((userId));
                    subscribeCategoryService.save(subscribeCategory);
                    text = String.format("email: %s успешно подписан на категорию: %s",
                            profileDto.getEmail(), parts[0]);
                } else {
                    text = "Профиль не найден";
                }
            } else {
                text = "Категория не найдена";
            }
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage(), e);
        }
        return text;
    }

    protected String getTextFromCategories() {
        StringBuilder text = new StringBuilder();
        text.append(String.format("%-5s | %-10s %s", "category_id", "category_name", SL));
        categoryService.getCategories().forEach(
                category -> text.append(String.format("%-22d | %-10s %s", category.getId(), category.getName(), SL))
        );
        return text.toString();
    }
}
