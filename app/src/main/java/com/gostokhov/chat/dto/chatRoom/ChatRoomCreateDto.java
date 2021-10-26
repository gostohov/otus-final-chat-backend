package com.gostokhov.chat.dto.chatRoom;

import com.gostokhov.chat.enumiration.ChatRoomType;
import lombok.Data;

import java.util.Set;

@Data
public class ChatRoomCreateDto {
    String name;
    String description;
    ChatRoomType type;
    Set<String> usernameList;
}
