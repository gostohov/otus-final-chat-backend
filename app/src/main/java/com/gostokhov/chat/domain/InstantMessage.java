package com.gostokhov.chat.domain;

import com.datastax.oss.driver.shaded.guava.common.base.Strings;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("messages")
public class InstantMessage {

    @PrimaryKeyColumn(name = "chatRoomId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String chatRoomId;

    @PrimaryKeyColumn(name = "date", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private Date date;

    private String username;
    private String fromUser;
    private String toUser;
    private String text;

    public InstantMessage() {
        this.date = new Date();
    }

    public boolean isPublic() {
        return Strings.isNullOrEmpty(this.toUser);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chatRoomId == null) ? 0 : chatRoomId.hashCode());
        result = prime * result + ((fromUser == null) ? 0 : fromUser.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((toUser == null) ? 0 : toUser.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InstantMessage other = (InstantMessage) obj;
        if (chatRoomId == null) {
            if (other.chatRoomId != null)
                return false;
        } else if (!chatRoomId.equals(other.chatRoomId))
            return false;
        if (fromUser == null) {
            if (other.fromUser != null)
                return false;
        } else if (!fromUser.equals(other.fromUser))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (toUser == null) {
            if (other.toUser != null)
                return false;
        } else if (!toUser.equals(other.toUser))
            return false;
        if (username == null) {
            return other.username == null;
        } else return username.equals(other.username);
    }
}
