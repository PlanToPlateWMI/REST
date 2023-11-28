drop table if exists recipe_ingredient;
drop table if exists invite_code;

drop table if exists app_user;
drop table if exists shop_product_group;
drop table if exists group_recipe;
drop table if exists recipe_ingredient;


drop table if exists meal_ingredient;
drop table if exists meal;
drop table if exists product;
drop table if exists recipe;
drop table if exists recipe_category;
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
    state varchar check (state in ('BUY', 'BOUGHT', 'PANTRY'))
);



create table user_group
(
    id bigint generated by default as identity,
    name varchar(256) default 'albina',
    primary key(id)
);


alter table app_user
    add constraint app_user_user_group_FK foreign key (group_id) references user_group;


alter table invite_code
    add constraint invite_code_app_user_FK foreign key (group_id) references user_group;

alter table product
    add constraint group_productFK foreign key (group_created_id) references user_group;

alter table shop_product_group
    add constraint group_owner_shop_productFK foreign key (group_owner_id) references user_group;

alter table app_user
    add constraint unique_email unique (email);


alter table shop_product_group
    add constraint product_shopFK foreign key (product_id) references product;

alter table product
    add constraint category_productFK foreign key (category_id) references category;

create table recipe_category
(
    id bigint generated by default as identity,
    title varchar(256) not null unique,
    primary key (id)
);


comment on table recipe_category is 'Tabel to store existing categories of recipe data';
comment on column recipe_category.id is 'Auto generated incremented primary key';
comment on column recipe_category.title is 'Title of category (e.g. lunch)';


create table recipe
(
    id bigint generated by default as identity,
    title varchar(1024) not null,
    description varchar(2056),
    level varchar(8) check (level in ('EASY', 'MEDIUM', 'HARD')),
    time int not null,
    source varchar(512),
    image_source varchar(512),
    steps varchar(4096) not null,
    portions int not null,
    is_vege boolean not null,
    category_id bigint,
    group_id bigint,
    primary key (id)
);

comment on table recipe is 'Tabel to store recipe data';
comment on column recipe.id is 'Auto generated inceremented primary key';
comment on column recipe.title is 'Title of recipe (e.g. cheesecake)';
comment on column recipe.description is 'Optional description of recipe (e.g. Cheesecake is a sweet dessert made with a soft fresh cheese , eggs, and sugar)';
comment on column recipe.level is 'Level of complexity of recipe';
comment on column recipe.time is 'Time to cook recipe in minutes';
comment on column recipe.source is 'Optional link to source of recipe';
comment on column recipe.image_source is 'Optional link to image source of recipe';
comment on column recipe.steps is 'Steps of recipe seperated by &';
comment on column recipe.portions is 'Number of portions of recipe';
comment on column recipe.is_vege is 'Is recipe vegetarian';
comment on column recipe.category_id is 'Suitable for vegetarians';
comment on column recipe.group_id is 'FK of owner group id (for general recipes - group of admins)';

alter table recipe
    add constraint recipe_group_id_FK foreign key (group_id) references user_group;

alter table recipe
    add constraint recipe_category_id_FK foreign key (category_id) references recipe_category;

create table group_recipe
(
    id bigint generated by default as identity,
    recipe_id bigint,
    group_id bigint,
    primary key (id)
);

comment on table group_recipe is 'Tabel to store recipe that are selected by specific group';
comment on column group_recipe.id is 'Auto generated inceremented primary key';
comment on column group_recipe.recipe_id is 'FK to recipe table';
comment on column group_recipe.group_id is 'FK to recipe table';

alter table group_recipe
    add constraint group_recipe_group_id_FK foreign key (group_id) references user_group;


alter table group_recipe
    add constraint group_recipe_recipe_id_FK foreign key (recipe_id) references recipe;


create table recipe_ingredient
(
    recipe_id bigint,
    ingredient_id bigint,
    qty float,
    primary key (recipe_id, ingredient_id)
);

comment on table recipe_ingredient is 'Tabel to store ingredients of recipe';
comment on column recipe_ingredient.recipe_id is 'FK to recipe table';
comment on column recipe_ingredient.ingredient_id is 'FK to product table';
comment on column recipe_ingredient.qty is 'Quantity of ths ingredient in recipe';

alter table recipe_ingredient
    add constraint recipe_ingredient_recipe_id_FK foreign key (recipe_id) references recipe;

alter table recipe_ingredient
    add constraint recipe_ingredient_product_id_FK foreign key (ingredient_id) references product;

create table meal (
                      id bigint,
                      recipe_id bigint,
                      portions int,
                      meal_type varchar(32) check (meal_type in ('BREAKFAST', 'LUNCH', 'DINNER')),
                      date date,
                      group_id bigint,
                      primary key (id)
);

alter table meal
    add constraint meal_recipe_id_FK foreign key (recipe_id) references recipe;

alter table meal
    add constraint meal_group_id_FK foreign key (group_id) references user_group;

create table meal_ingredient (
                                 meal_id bigint,
                                 ingredient_id bigint,
                                 qty float,
                                 primary key (meal_id, ingredient_id)
);

alter table meal_ingredient
    add constraint meal_ingredient_meal_id_FK foreign key (meal_id) references meal;

alter table meal_ingredient
    add constraint meal_ingredient_product_id_FK foreign key (ingredient_id) references product;
