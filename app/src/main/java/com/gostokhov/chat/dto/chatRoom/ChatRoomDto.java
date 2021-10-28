package com.gostokhov.chat.dto.chatRoom;

import com.gostokhov.chat.dto.user.UserDto;
import com.gostokhov.chat.enumiration.ChatRoomType;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ChatRoomDto {
    Long id;
    String name;
    String description;
    ChatRoomType type;
    Date lastTimeUpdated;
    Set<UserDto> users;
}
