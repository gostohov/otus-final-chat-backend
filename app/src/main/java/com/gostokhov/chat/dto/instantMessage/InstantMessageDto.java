package com.gostokhov.chat.dto.instantMessage;

import lombok.Data;

import java.util.Date;

@Data
public class InstantMessageDto {
    Long chatRoomId;
    Date date;
    String authorUsername;
    String recipientUsername;
    String content;
}
