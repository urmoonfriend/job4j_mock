package ru.checkdev.notification.domain.kafka;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.checkdev.notification.domain.http.RoleDTO;

import java.util.Calendar;
import java.util.List;

@Data
@Accessors(chain = true)
public class AuthPersonDto {
    private String email;
    private String password;
    private boolean privacy;
    private List<RoleDTO> roles;
    private Calendar created;
    private String chatId;
}
