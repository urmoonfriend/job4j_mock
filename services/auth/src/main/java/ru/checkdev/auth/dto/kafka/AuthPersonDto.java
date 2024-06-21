package ru.checkdev.auth.dto.kafka;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.checkdev.auth.domain.Role;

import java.util.Calendar;
import java.util.List;

@Data
@Accessors(chain = true)
public class AuthPersonDto {
    private String email;
    private String password;
    private boolean privacy;
    private List<Role> roles;
    private Calendar created;
    private String chatId;
}