package com.gostokhov.chat.dto.instantMessage;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class InstantMessageDto {
    Long chatRoomId;
    Date date;
    String authorUsername;
    Set<String> recipientUsernameList;
    String content;
}
