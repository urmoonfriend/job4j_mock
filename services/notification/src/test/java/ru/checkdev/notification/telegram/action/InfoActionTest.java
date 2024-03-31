package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InfoActionTest {

    @Autowired
    private InfoAction infoAction;

    private static Message message;

    private final List<String> actions = List.of(
            "/start", "/new", "/check", "/forget", "/subscribe", "/unsubscribe");

    @BeforeAll
    public static void setMessage() {
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    public void whenHandleThenOk() {
        String sl = System.lineSeparator();
        var out = new StringBuilder();
        out.append("Выберите действие:").append(sl);
        for (String action : actions) {
            out.append(action).append(sl);
        }
        var result = infoAction.handle(message);
        assertThat(result).isEqualTo(new SendMessage(message.getChatId().toString(), out.toString()));
    }
}
