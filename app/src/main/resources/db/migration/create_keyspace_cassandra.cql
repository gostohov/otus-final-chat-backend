CREATE KEYSPACE chat WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE chat;
CREATE TABLE IF NOT EXISTS messages (
    username text,
    chatRoomId text,
    date timestamp,
    fromUser text,
    toUser text,
    text text,
    PRIMARY KEY ((chatRoomId), date)
) WITH CLUSTERING ORDER BY (date ASC);

USE chat;
CREATE TABLE IF NOT EXISTS chatrooms (
    id text,
    name text,
    description text,
    date timestamp,
    PRIMARY KEY ((id), date)
) WITH CLUSTERING ORDER BY (date ASC);
