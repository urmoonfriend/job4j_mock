package ru.checkdev.notification.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class CategoryDto {
    @EqualsAndHashCode.Include
    private int id;
    private String name;
    private int total;
    private int position;
}