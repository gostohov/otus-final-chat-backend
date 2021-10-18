package com.gostokhov.chat.dto.user;

import com.gostokhov.chat.dto.userChatRoom.UserChatRoomDto;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class UserDto {
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
    Set<UserChatRoomDto> chatRooms;
}
