package com.gostokhov.chat.dto.user;

import com.gostokhov.chat.dto.chatRoom.ChatRoomDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCurrentDto extends UserDto {
    Set<ChatRoomDto> chatRooms;
}
