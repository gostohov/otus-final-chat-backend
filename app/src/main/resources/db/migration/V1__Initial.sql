create database if not exists chat;

create table if not exists user
(
    id                      bigint not null auto_increment,
    authorities             tinyblob,
    email                   varchar(255),
    first_name              varchar(255),
    is_active               bit,
    is_not_locked           bit,
    join_date               datetime(6),
    last_login_date         datetime(6),
    last_login_date_display datetime(6),
    last_name               varchar(255),
    password                varchar(255),
    image_url               varchar(255),
    roles                   varchar(255),
    username                varchar(255),
    primary key (id)
) engine = innodb;

create table if not exists chat_room
(
    id                bigint not null auto_increment,
    name              varchar(255),
    description       varchar(255),
    type              varchar(255),
    last_time_updated datetime(6),
    primary key (id)
) engine = innodb;

create table if not exists user_chat_room
(
    id           bigint not null auto_increment,
    user_id      bigint NOT NULL,
    chat_room_id bigint NOT NULL,
    primary key (id),
    key chat_room_id (chat_room_id),
    constraint employee_project_ibfk_1
        foreign key (user_id) references user (id),
    constraint employee_project_ibfk_2
        foreign key (chat_room_id) references chat_room (id)
) engine = innodb
  default charset = utf8;
