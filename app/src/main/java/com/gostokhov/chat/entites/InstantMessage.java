package com.gostokhov.chat.entites;

import lombok.*;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;
import java.util.Objects;

@Setter
@Getter
@ToString
@Table("messages")
public class InstantMessage {

    @PrimaryKeyColumn(name = "chatRoomId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long chatRoomId;

    @PrimaryKeyColumn(name = "date", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private Date date;

    private String authorUsername;
    private String recipientUsername;
    private String content;

    public InstantMessage(Long chatRoomId, String authorUsername, String recipientUsername, String content) {
        this.chatRoomId = chatRoomId;
        this.date = new Date();
        this.authorUsername = authorUsername;
        this.recipientUsername = recipientUsername;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstantMessage)) return false;
        InstantMessage that = (InstantMessage) o;
        return chatRoomId.equals(that.chatRoomId) && date.equals(that.date) && authorUsername.equals(that.authorUsername) && content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, date, authorUsername, content);
    }
}
