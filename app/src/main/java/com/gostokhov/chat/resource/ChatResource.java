package com.gostokhov.chat.resource;

import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = {"/", "/chat"})
public class ChatResource {

    private ChatRoomService chatRoomService;

    @Autowired
    public ChatResource(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/list")
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomService.findAll();
    }
}
