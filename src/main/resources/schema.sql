drop table if exists app_user;
drop table if exists shop_product_group;
drop table if exists invite_code;
drop table if exists product;
drop table if exists user_group;
drop table if exists category;

create table app_user
(
    id bigint generated by default as identity,
    email varchar(255) not null unique ,
    username varchar(255) not null ,
    password varchar(255) not null ,
    role varchar(255) check (role in ('ROLE_USER', 'ROLE_ADMIN')),
    group_id bigint,
    is_active bool,
    primary key (id)
);


create table invite_code
(
    id bigint generated by default as identity,
    code integer not null unique ,
    role varchar(255) check (role in ('ROLE_USER', 'ROLE_ADMIN')),
    expired_time time,
    group_id bigint not null ,
    primary key (id)
);


create table category(
    id bigint generated by default as identity,
    category varchar(255) not null unique ,
    primary key (id)
);


create table product(
    id bigint generated by default as identity,
    name varchar(255) not null ,
    category_id bigint not null ,
    group_created_id bigint not null ,
    unit varchar(255) not null check (unit in ('L', 'KG', 'GR', 'SZT', 'ML')),
    primary key (id)
);


create table shop_product_group(
    id bigint generated by default as identity,
    amount float check (amount > 0),
    product_id bigint not null ,
    group_owner_id bigint not null ,
    state varchar(255) check ( state in ( 'BUY', 'BOUGHT', 'PANTRY' ))
);



create table user_group
(
    id bigint generated by default as identity,
    name varchar(256) default 'albina',
    primary key (id)
);



alter table app_user
    add constraint unique_email unique (email);


alter table app_user
    add constraint app_user_user_group_FK foreign key (group_id) references user_group;


alter table invite_code
    add constraint invite_code_app_user_FK foreign key (group_id) references user_group;

alter table product
    add constraint group_productFK foreign key (group_created_id) references user_group;

alter table product
    add constraint category_productFK foreign key (category_id) references category;


alter table shop_product_group
    add constraint group_owner_shop_productFK foreign key (group_owner_id) references user_group;

alter table shop_product_group
    add constraint product_shopFK foreign key (product_id) references product;


