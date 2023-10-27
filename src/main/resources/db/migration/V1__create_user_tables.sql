create table app_user
(
    id bigint generated by default as identity,
    email varchar(255) not null unique ,
    username varchar(255) not null ,
    password varchar(255) not null ,
    role varchar(255) check (role in ('ROLE_USER', 'ROLE_ADMIN')),
    group_id bigint,
    is_active bool,
    fcm_token varchar(255) default 'token',
    primary key (id)
);

create table invite_code
(
    id bigint generated by default as identity,
    code integer not null unique ,
    role varchar(255) check (role in ('ROLE_USER', 'ROLE_ADMIN')),
    expired_time timestamp,
    group_id bigint not null ,
    primary key (id)
);

create table user_group
(
    id bigint generated by default as identity,
    name varchar(256) default 'group',
    primary key (id)
);

alter table app_user
    add constraint unique_email unique (email);

alter table app_user
    add constraint app_user_user_group_FK foreign key (group_id) references user_group;

alter table invite_code
    add constraint invite_code_app_user_FK foreign key (group_id) references user_group;