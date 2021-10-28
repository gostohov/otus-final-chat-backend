package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.UserChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    UserChatRoom findUserChatRoomByUserId(Long userId);

    UserChatRoom findUserChatRoomByChatRoomId(Long chatRoomId);

    @Query("SELECT userChatRoom FROM UserChatRoom userChatRoom WHERE userChatRoom.user.id=:user_id")
    List<UserChatRoom> findAllByUserId(@Param("user_id") Long userId);
}
