create table USER
(
    ID         bigint primary key auto_increment,
    NAME       varchar(100),
    TEL        varchar(20) unique,
    AVATAR_URL varchar(1024),
    CREATED_AT timestamp,
    UPDATED_AT timestamp
)