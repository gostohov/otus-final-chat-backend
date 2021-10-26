package com.gostokhov.chat.repository;

import com.gostokhov.chat.entites.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findChatRoomById(Long id);

    @Query("""
        select distinct cr
        from ChatRoom cr
        join fetch cr.users
        where exists (
            select ucr
            from UserChatRoom ucr
            join UserChatRoom ucr2 on ucr.chatRoomId = ucr2.chatRoomId and ucr2.userId in :ids and ucr.userId <> ucr2.userId
            where ucr.chatRoomId = cr.id and ucr.userId in :ids
        )
    """)
    List<ChatRoom> findByUsersIdIn(@Param("ids") List<Long> ids);
}
