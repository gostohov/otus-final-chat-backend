package com.gostokhov.chat.dto.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    String userId;
    String firstName;
    String lastName;
    String username;
    String email;
    String profileImageUrl;
    Date lastLoginDate;
    Date lastLoginDateDisplay;
    Date joinDate;
    String roles;
    String[] authorities;
    Boolean isActive;
    Boolean isNotLocked;
}
