package ru.checkdev.notification.telegram.action;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RegActionTest {
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
        Optional<BotApiMethod<Message>> result = action.callback(message);
        Optional<SendMessage> sendMessageOptional = result.map(botApiMethod -> (SendMessage) botApiMethod);
        assertThat(sendMessageOptional.get().getText()).isEqualTo(String.format("Email: %s не корректный.%s"
                + "попробуйте снова.%s"
                + "/new", incorrectEmail, SL, SL));
    }

}
