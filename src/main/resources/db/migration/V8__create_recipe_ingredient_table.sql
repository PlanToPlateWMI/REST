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