create database if not exists chat;
create table if not exists users (
    id bigint not null auto_increment,
    authorities tinyblob,
    email varchar(255),
    first_name varchar(255),
    is_active bit,
    is_not_locked bit,
    join_date datetime(6),
    last_login_date datetime(6),
    last_login_date_display datetime(6),
    last_name varchar(255),
    password varchar(255),
    profile_image_url varchar(255),
    roles varchar(255),
    user_id varchar(255),
    username varchar(255),
    primary key (id)
) engine=innodb
