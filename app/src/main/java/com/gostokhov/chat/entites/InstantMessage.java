package com.gostokhov.chat.entites;

import com.datastax.oss.driver.shaded.guava.common.base.Strings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Setter
@Getter
@RequiredArgsConstructor
@ToString
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

    public boolean isPublic() {
        return Strings.isNullOrEmpty(this.toUser);
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
