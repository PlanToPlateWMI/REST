create table recipe_category
(
    id bigint generated by default as identity,
    title varchar(256) not null unique,
    primary key (id)
);


comment on table recipe_category is 'Tabel to store existing categories of recipe data';
comment on column recipe_category.id is 'Auto generated incremented primary key';
comment on column recipe_category.title is 'Title of category (e.g. lunch)';


create table recipe_recipe_category
(
    category_id bigint,
    recipe_id bigint
);

comment on table recipe_recipe_category is 'Tabel to store categories of recipe ';
comment on column recipe_recipe_category.category_id is 'FK to category table';
comment on column recipe_recipe_category.recipe_id is 'FK to recipe table';


alter table recipe_recipe_category
    add constraint recipe_category_category_id_FK foreign key (category_id) references recipe_category;

alter table recipe_recipe_category
    add constraint recipe_category_recipe_id_FK foreign key (recipe_id) references recipe;