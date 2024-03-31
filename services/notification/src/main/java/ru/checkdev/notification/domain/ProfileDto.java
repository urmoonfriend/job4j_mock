package ru.checkdev.notification.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Calendar;

@Data
@Accessors(chain = true)
public class ProfileDto {
    private int id;
    private String username;
    private String email;
    private String key;
    private String password;
    private boolean active;
    private String experience;
    private boolean show;
    private String salary;
    private String aboutShort;
    private String about;
    private boolean privacy;
    private String brief;
    private String urlHh;
    private String location;
    private Calendar updated;
    private Calendar created;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileDto profile = (ProfileDto) o;
        return id == profile.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}