ALTER TABLE recipe_category
ALTER COLUMN id
        RESTART WITH 1;

ALTER TABLE recipe
    ALTER COLUMN id
        RESTART WITH 1;

insert into recipe_category (title) values
('napoje'),
('zupy'),
('desery'),
('danie główne'),
('przystawki'),
('wege');


insert into recipe (title, time, level, portions, steps, source, image_source) values
('Spice latte', 10, 'EASY', 1, 'Krok 1: Mleko podgrzać z przyprawami (cynamonem, imbirem, gałką muszkatołową). & Krok 2: Do gorącego mleka dodać wodę i kawę  i dokładnie wymieszać. & Krok 3: Latte przelać do szklanki, udekorować bitą śmietaną i oprószyć cynamonem.',
 'https://www.kwestiasmaku.com/zielony_srodek/dynia/pumpkin_spice_latte/przepis.html',
 'https://www.kwestiasmaku.com/sites/v123.kwestiasmaku.com/files/pumpkin-spice-latte-01.jpg');

insert into recipe (title, time, level, portions, steps, source, image_source) values
('Zupa dyniowa z curry', 30, 'MEDIUM', 4, 'Krok 1: Dynię obrać ze skórki, usunąć nasiona, miąższ pokroić w kostkę. Ziemniaki obrać i też pokroić w kostkę. & Krok 2: W większym garnku na oliwie zeszklić pokrojoną w kosteczkę cebulę oraz obrany i suszony czosnek. Dodać dynię i ziemniaki, doprawić solą i smażyć co chwilę mieszając przez około 5 minut. Pod koniec dodać curry i kurkumę. & Krok 3: Wlać gorący bulion, przykryć i zagotować. Zmniejszyć ogień do średniego i gotować przez około 10 minut. & Krok 4: Świeże pomidory sparzyć, obrać, pokroić na ćwiartki, usunąć szypułki oraz nasiona z komór. Miąższ pokroić w kosteczkę i dodać do zupy. & Krok 5: Wymieszać, przykryć i gotować przez ok. 10 minut lub dłużej, do miękkości warzyw. Zmiksować w blenderze z dodatkiem śmietanki (zachować parę łyżeczek do skropienia zupy). & Krok 6: Zupę przed podaniem posypać posiekaną bazylią.',
 'https://www.kwestiasmaku.com/przepis/zupa-dyniowa-z-curry',
 'https://www.kwestiasmaku.com/sites/v123.kwestiasmaku.com/files/zupa_dyniowa_z_curry_01.jpg');

insert into recipe (title, time, level, portions, steps, source, image_source) values
('Matcha brownie', 40, 'MEDIUM', 4, 'Krok 1: Piekarnik nagrzać do 175 stopni C. Małą blaszkę o wymiarach 20 cm x 24 cm (lub o podobnej powierzchni) wyłożyć papierem do pieczenia. & Krok 2: Masło roztopić na ogniu w rondelku. & Krok 3: Czekoladę połamać na kosteczki i włożyć do miski. Zalać roztopionym masłem, dodać matchę i wymieszać do rozpuszczenia. & Krok 4: Jajka ogrzać (np. w ciepłej wodzie), wbić do czystej miski, dodać cukier oraz sól i ubijać przez ok. 5 - 7 minut na gęstą i puszystą pianę. & Krok 5: Do ubitych jajek dodać przestudzoną masę czekoladową w 3 partiach, za każdym razem delikatnie mieszając łyżką do połączenia się składników. & Krok 6: Do miski z masą przesiać mąkę pszenną i ponownie wymieszać na jednolitą masę. & Krok 7: Wylać do formy i wstawić do piekarnika na 20 minut. Wyjąć z piekarnika i ostudzić.',
 'https://www.kwestiasmaku.com/przepis/matcha-brownie',
 'https://www.kwestiasmaku.com/sites/v123.kwestiasmaku.com/files/matcha-brownie-01.jpg');

insert into recipe (title, time, level, portions, steps, source, image_source) values
('Chili con carne', 45, 'HARD', 3, 'Krok 1: Na dużej patelni, na oliwie zeszklić pokrojoną w kosteczkę cebulę, dodać czosnek, paprykę, kmin rzymski i oregano i mieszając smażyć przez 1 minutę. Stopniowo dodawać zmieloną wołowinę. & Krok 2: Pomidory należy sparzyć, obrać, pokroić na ćwiartki, usunąć pestki, miąższ pokroić w kosteczkę i dodać do mięsa. Wymieszać i doprawić cukrem, pieprzem oraz szczyptą soli. & Krok 3: Przykryć i dusić przez około 20 na umiarkowanym ogniu, co jakiś czas zamieszać. Dodać pokrojoną w kosteczkę paprykę. Gotować pod przykryciem przez około 10 minut, od czasu do czasu zamieszać. Na koniec dodać fasolę i gotować pod przykryciem przez ok. 3 minuty. & Krok 4: Na koniec gotować potrawę podlewając stopniowo wrzącym bulionem lub wodą, tak aby powstało trochę gęstego sosu. Można przygotować wcześniej i odgrzewać. Krok 5: Najlepiej podawać razem z gotowanym ryżem lub makaronem.',
 'https://www.kwestiasmaku.com/dania_dla_dwojga/chili_con_carne/przepis.html',
 'https://www.kwestiasmaku.com/sites/v123.kwestiasmaku.com/files/chili_con_carne_01_0.jpg');

insert into recipe (title, time, level, portions, steps, source, image_source) values
('Sałatka z serkiem kozim i awokado', 10, 'EASY', 1, 'Krok 1: Jagody opłukać pod delikatnym strumieniem wody, osuszyć na papierowym ręczniku. & Krok 2: Włożyć do salaterki, dodać obrane i pokrojone na kawałki awokado oraz pokruszony ser kozi. & Krok 3: Dodać bazylię, polać dressingiem (miód i sos balsamiczny) i delikatnie wymieszać.',
 'https://www.kwestiasmaku.com/zielony_srodek/jagody/salatka_z_jagodami/przepis.html',
 'https://www.kwestiasmaku.com/sites/v123.kwestiasmaku.com/files/salatka-z-jagodami-02.jpg');

insert into recipe (title, time, level, portions, steps, source, image_source) values
('Pieczony kalafior ', 35, 'MEDIUM', 4, 'Krok 1: Odciąć liście z kalafiora, przekroić na pół i wyciąć nadmiar głąba. Główkę kalafiora rozdzielić na różyczki lub pokroić na cząstki. & Krok 2: Położyć na dużej blaszce (np. z wyposażenia piekarnika) wyłożonej papierem do pieczenia. Piekarnik nagrzać do 200 stopni C. & Krok 3: Kalafiora polać oliwą, posypać pieprzem, kurkumą i czarnuszką, a następnie dokładnie wymieszać i rozłożyć na całej powierzchni blachy. & Krok 4: Wstawić do nagrzanego piekarnika i piec przez 30 minut. Po upieczeniu doprawić solą i posiekaną natką pietruszki.',
 'https://aniagotuje.pl/przepis/pieczony-kalafior',
 'https://cdn.aniagotuje.com/pictures/articles/2020/05/4181749-v-720x991.jpg');

insert into recipe_recipe_category(recipe_id, category_id) values (1,1);
insert into recipe_recipe_category(recipe_id, category_id) values (2,2);
insert into recipe_recipe_category(recipe_id, category_id) values (3,3);
insert into recipe_recipe_category(recipe_id, category_id) values (4,4);
insert into recipe_recipe_category(recipe_id, category_id) values (5,5);
insert into recipe_recipe_category(recipe_id, category_id) values (6,6);