package ru.checkdev.notification.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MailGunTest {

    @Test
    public void whenInvalidDateThenThrowParseException() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yy, HH:mm", new Locale("ru"));

        String invalidDate = "32 авг 18, 05:15"; // Invalid day of month
        assertThrows(java.text.ParseException.class, () -> {
            sdf.parse(invalidDate);
        });
    }
}
