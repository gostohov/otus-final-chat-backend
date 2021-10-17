package com.gostokhov.chat.dto.user;

import lombok.Data;

@Data
public class UserDtoRegister {
    String firstName;
    String lastName;
    String username;
    String email;
    String password;
}
