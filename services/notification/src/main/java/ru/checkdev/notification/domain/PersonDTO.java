package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Calendar;
import java.util.List;

/**
 * DTO модель класса Person сервиса Auth.
 *
 * @author parsentev
 * @since 25.09.2016
 */
@Data
@Accessors(chain = true)
public class PersonDTO {
    private String email;
    private String password;
    private boolean privacy;
    private List<RoleDTO> roles;
    private Calendar created;

}
