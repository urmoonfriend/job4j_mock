package ru.checkdev.notification.telegram.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.client.TgAuthCallWebClint;
import ru.checkdev.notification.domain.dto.CategoryDto;
import ru.checkdev.notification.service.CategoryService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class SubscribeActionTest {

    @Autowired
    private SubscribeAction action;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    private static Message message;

    private static final String SL = System.lineSeparator();

    @MockBean
    private CategoryService categoryService;

    @BeforeAll
    public static void setMessage() {
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    void handle() {
        when(categoryService.getCategories()).thenReturn(
                List.of(
                        new CategoryDto().setId(0).setName("category0").setPosition(0).setTotal(1),
                        new CategoryDto().setId(1).setName("category1").setPosition(1).setTotal(2),
                        new CategoryDto().setId(2).setName("category2").setPosition(2).setTotal(3)
                ));
        String out = "Для подписки введите данные в формате " + SL
                + "category_id:email:password" + SL + SL
                + "Список доступных категорий: " + SL + SL + action.getTextFromCategories();
        var result = action.handle(message);
        assertThat(result).isEqualTo(new SendMessage(message.getChatId().toString(), out));
    }

    @Test
    void callbackWhenEmailIncorrect() {
        String incorrectEmail = "incorrectEmail.ru";
        message.setText(incorrectEmail);
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText()).isEqualTo("Нужно передать параметры в формате category_id:email:password");
    }

    @Test
    void callbackWhenCategoryNotFound() {
        message.setText("1:correct@mail.ru:123");
        Map<String, String> map = Map.of("someAttribute", "someValue");
        Object object =  new ObjectMapper().convertValue(map, Object.class);
        when(tgAuthCallWebClint.doPost(anyString(), any()))
                .thenReturn(Mono.just(object));
        when(categoryService.categoryExists(any())).thenReturn(false);
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText())
                .isEqualTo("Категория не найдена");
    }

    @Test
    void callbackWhenProfileNotFound() {
        message.setText("1:correct@mail.ru:123");
        Map<String, String> map = Map.of("someAttribute", "someValue");
        Object object =  new ObjectMapper().convertValue(map, Object.class);
        when(tgAuthCallWebClint.doPost(anyString(), any()))
                .thenReturn(Mono.just(object));
        when(categoryService.categoryExists(any())).thenReturn(true);
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText())
                .isEqualTo("Профиль не найден");
    }

    @Test
    void callbackOk() {
        String email = "correct@mail.ru";
        String password = "123";
        String categoryId = "1";
        message.setText(String.format("%s:%s:%s", categoryId, email, password));
        Map<String, String> map = Map.of("id", "2");
        Object object =  new ObjectMapper().convertValue(map, Object.class);
        when(tgAuthCallWebClint.doPost(anyString(), any()))
                .thenReturn(Mono.just(object));
        when(categoryService.categoryExists(any())).thenReturn(true);
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText())
                 .isEqualTo(String.format("email: %s успешно подписан на категорию: %s", email, categoryId));
    }
}