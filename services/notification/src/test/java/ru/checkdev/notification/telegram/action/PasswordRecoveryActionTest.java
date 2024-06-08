package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.client.TgAuthCallWebClint;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordRecoveryActionTest {

    @Autowired
    private PasswordRecoveryAction action;

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;

    private static Message message;

    @BeforeAll
    public static void setMessage() {
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    void handle() {
        String out = "Введите email для сброса пароля:";
        var result = action.handle(message);
        assertThat(result).isEqualTo(new SendMessage(message.getChatId().toString(), out));
    }

    @Test
    void callback() {
    }
}