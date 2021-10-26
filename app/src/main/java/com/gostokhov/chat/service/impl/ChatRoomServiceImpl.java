package com.gostokhov.chat.service.impl;

import com.gostokhov.chat.entites.ChatRoom;
import com.gostokhov.chat.entites.User;
import com.gostokhov.chat.entites.UserChatRoom;
import com.gostokhov.chat.enumiration.ChatRoomType;
import com.gostokhov.chat.exception.domain.UserNotFoundException;
import com.gostokhov.chat.repository.ChatRoomRepository;
import com.gostokhov.chat.repository.UserChatRoomRepository;
import com.gostokhov.chat.service.ChatRoomService;
import com.gostokhov.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;

    @Override
    @Transactional
    public ChatRoom createPrivateChatRoom(String username) throws UserNotFoundException {
        User user = userService.findUserByUsername(username);
        if (Objects.nonNull(user)) {
            User currentUser = userService.getCurrentUser();

            ChatRoom chatRoom = chatRoomRepository.save(
                ChatRoom.builder().type(ChatRoomType.PRIVATE).build()
            );

            userChatRoomRepository.save(new UserChatRoom(user, chatRoom));
            userChatRoomRepository.save(new UserChatRoom(currentUser, chatRoom));
            return chatRoom;
        }
        return null;
    }

    @Override
    @Transactional
    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> findChatRoomListByUserIds(List<Long> userIds) {
        return chatRoomRepository.findByUsersIdIn(userIds);
    }

    @Override
    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepository.findChatRoomById(id);
    }
}
