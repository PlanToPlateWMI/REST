create table recipe_ingredients
(
    recipe_id bigint,
    ingredients_id bigint,
    qty float,
    primary key (recipe_id, ingredients_id)
);

comment on table recipe_ingredients is 'Tabel to store ingredients of recipe';
comment on column recipe_ingredients.recipe_id is 'FK to recipe table';
comment on column recipe_ingredients.ingredients_id is 'FK to product table';
comment on column recipe_ingredients.qty is 'Quantity of ths ingredient in recipe';

alter table recipe_ingredients
    add constraint recipe_ingredient_recipe_id_FK foreign key (recipe_id) references recipe;

alter table recipe_ingredients
    add constraint recipe_ingredient_product_id_FK foreign key (ingredients_id) references product;