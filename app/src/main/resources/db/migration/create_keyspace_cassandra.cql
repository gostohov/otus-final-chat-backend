CREATE KEYSPACE chat WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE chat;
CREATE TABLE IF NOT EXISTS messages
(
    chatRoomId        bigint,
    date              timestamp,
    authorUsername    text,
    recipientUsername text,
    content           text,
    PRIMARY KEY ((chatRoomId), date)
) WITH CLUSTERING ORDER BY (date ASC);
