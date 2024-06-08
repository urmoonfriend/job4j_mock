package ru.checkdev.notification.telegram.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.client.TgAuthCallWebClint;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class RegActionTest {

    @Autowired
    private RegAction action;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    private static Message message;

    private static final String SL = System.lineSeparator();
    @Value("${server.site.url.login}")
    private String urlSiteAuth;

    @BeforeAll
    public static void setMessage() {
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    void handle() {
        String out = "Введите email для регистрации:";
        var result = action.handle(message);
        assertThat(result).isEqualTo(new SendMessage(message.getChatId().toString(), out));
    }

    @Test
    void callbackWhenEmailIncorrect() {
        String incorrectEmail = "incorrectEmail.ru";
        message.setText(incorrectEmail);
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText()).isEqualTo(String.format("Email: %s не корректный.%s"
                + "попробуйте снова.%s"
                + "/new", incorrectEmail, SL, SL));
    }

    @Test
    void callbackWhenError() {
        message.setText("correct@mail.ru");
        Map<String, String> map = Map.of("error", "someError");
        Object object =  new ObjectMapper().convertValue(map, Object.class);
        when(tgAuthCallWebClint.doPost(anyString(), any()))
                .thenReturn(Mono.just(object));
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(result.getText())
                .isEqualTo(String.format("Ошибка регистрации: %s", map.get("error")));
    }

    @Test
    void callbackOk() {
        String correctEmail = "correctEmail@mail.ru";
        String newPassword = "newPassword";
        message.setText(correctEmail);
        Map<String, String> map = Map.of("ok", newPassword);
        Object object =  new ObjectMapper().convertValue(map, Object.class);
        when(tgAuthCallWebClint.doPost(anyString(), any()))
                .thenReturn(Mono.just(object));
        SendMessage result = (SendMessage) action.callback(message);
        assertThat(true)
                .isEqualTo(result.getText().startsWith(
                        String.format("Вы зарегистрированы: %s"
                                + "Логин: %s%s", SL, correctEmail, SL)));
    }
}