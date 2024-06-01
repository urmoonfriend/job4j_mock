package ru.checkdev.auth.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CheckInfoDto {
    private String email;
    private String username;
}
