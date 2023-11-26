-- change Cytryna na Limonka
UPDATE product SET name = 'Limonka' where id = 42;
-- change recipe ingredient
UPDATE recipe_ingredient SET ingredient_id = 130 WHERE recipe_id = 9 and qty = 4;

UPDATE recipe SET steps = 'Krok 1: Ugotować fasolę &
Krok 2: Na dużej patelni, na oliwie zeszklić pokrojoną w kosteczkę cebulę, dodać czosnek, paprykę, kmin rzymski i oregano i
mieszając smażyć przez 1 minutę. Stopniowo dodawać zmieloną wołowinę. & Krok 3: Pomidory należy sparzyć, obrać,
pokroić na ćwiartki, usunąć pestki, miąższ pokroić w kosteczkę i dodać do mięsa. Wymieszać i doprawić cukrem,
pieprzem oraz szczyptą soli. & Krok 4: Przykryć i dusić przez około 20 na umiarkowanym ogniu, co jakiś czas
zamieszać. Dodać pokrojoną w kosteczkę paprykę. Gotować pod przykryciem przez około 10 minut, od czasu do czasu
zamieszać. Na koniec dodać fasolę i gotować pod przykryciem przez ok. 3 minuty. & Krok 5: Na koniec gotować potrawę
podlewając stopniowo wrzącym bulionem lub wodą, tak aby powstało trochę gęstego sosu. Można przygotować wcześniej
i odgrzewać. Krok 6: Najlepiej podawać razem z gotowanym ryżem lub makaronem.'
where title = 'Chili con carne';


INSERT INTO product (name, category_id, group_created_id, unit) VALUES
('Twaróg', 2, 1, 'GR'),
('Śmietanka', 2, 1, 'ML'),
('Malina', 5, 1, 'GR'),
('Nasiona chia', 7, 1, 'GR'),
('Filet z dorsza', 8, 1, 'KG'),
('Filet z kurczaka', 4, 1, 'KG'),
('Brokuł', 6, 1, 'KG'),
('Musztarda', 9, 1, 'GR'),
('Majeranek', 9, 1, 'GR'),
('Suszone podgrzybki', 9, 1, 'GR'),
('Przyprawa do kurczaka', 9, 1, 'GR'),
('Mięso mielone z indyka', 4, 1, 'KG'),
('Filet z piersi kurczaka', 4, 1, 'KG'),
('Płatki kukurydziane', 7, 1, 'GR'),
('Cukinia', 6, 1, 'KG'),
('Ser żółty', 2, 1, 'GR'),
('Tymianek', 9, 1, 'GR'),
('Sos sojowy', 9, 1, 'ML'),
('Pomidory koktajlowe', 6, 1, 'KG'),
('Filet łososia', 8, 1, 'KG'),
('Szparagi', 6, 1, 'KG'),
('Parmezan', 2, 1, 'GR'),
('Szczypiorek', 9, 1,  'GR'),
('Majonez', 9, 1, 'GR'),
('Tortilla', 3, 1, 'SZT'),
('Sałata', 6, 1, 'GR'),
('Serek śmietankowy', 2, 1, 'GR'),
('Jogurt grecki', 2, 1, 'ML'),
('Migdały', 10, 1, 'GR'),
('Łosoś wędzony', 8, 1, 'GR'),
('Sok cytrynowy', 9, 1, 'ML'),
('Ciasto francuskie', 3, 1, 'GR'),
('Filety śledziowe w oleju', 12, 1, 'GR'),
('Korniszony', 12, 1, 'GR'),
('Sos czosnkowy', 9, 1, 'ML'),
('Bulion warzywny (kostka)', 9, 1, 'SZT'),
('Papryka żółta', 6, 1, 'KG'),
('Papryka zielona', 6, 1, 'KG'),
('Śliwka', 5, 1, 'KG'),
('Płatki owsiane', 7, 1, 'GR'),
('Mleko ryżowe', 2, 1, 'ML'),
('Mąka gryczana', 7, 1, 'KG'),
('Mąka ziemniaczana', 7, 1, 'GR'),
('Otręby pszenne', 7, 1, 'GR'),
('Pomidory cherry', 6, 1, 'KG'),
('Bagietka', 3, 1, 'SZT'),
('Bazylia pęczek', 6, 1, 'SZT'),
('Sos sałatkowy włoski', 9, 1, 'SZT'),
('Syrop klonowy', 13, 1, 'ML'),
('Napój kokosowy', 11, 1, 'ML'),
('Kasza jaglana', 7, 1, 'GR'),
('Wiórki kokosowe', 7, 1, 'GR'),
('Mleko roślinne', 2, 1, 'L'),
('Ocet spirytusowy', 9, 1, 'ML'),
('Liście laurowe', 9, 1, 'GR'),
('Kurki', 13, 1, 'GR'),
('Szpinak', 6, 1, 'GR'),
('Rukkola', 6, 1, 'GR'),
('Bób', 6, 1, 'GR'),
('Proszek do pieczenia', 7, 1, 'GR'),
('Ekstrakt pomarańczowy', 11, 1, 'ML'),
('Czekolada mleczna', 2, 1, 'GR'),
('Kakao', 7, 1, 'GR'),
('Cukier puder', 7, 1, 'GR');


insert into recipe_ingredient(recipe_id,ingredient_id, qty) values
-- Sernik z białą czekoladą i malinami
(23, (SELECT id FROM product where name = 'Twaróg' and group_created_id = 1) , 750),
(23, (SELECT id FROM product where name = 'Jaja' and group_created_id = 1) , 3),
(23, (SELECT id FROM product where name = 'Cukier' and group_created_id = 1) , 150 ),
(23, (SELECT id FROM product where name = 'Śmietanka' and group_created_id = 1) , 125 ),
(23, (SELECT id FROM product where name = 'Biała czekolada' and group_created_id = 1) , 100),
(23, (SELECT id FROM product where name = 'Malina' and group_created_id = 1) , 200),
-- Chia pudding
(24 , (SELECT id FROM product where name = 'Nasiona chia' and group_created_id = 1) , 40),
(24 , (SELECT id FROM product where name = 'Mleko' and group_created_id = 1 ), 250 ),
(24 , (SELECT id FROM product where name = 'Cukier' and group_created_id = 1) , 10),
-- Dorsz w porach
(25, (SELECT id FROM product where name = 'Filet z dorsza' and group_created_id = 1) ,0.5 ),
(25, (SELECT id FROM product where name = 'Olej oliwkowy' and group_created_id = 1) , 100),
(25, (SELECT id FROM product where name = 'Masło' and group_created_id = 1) , 20),
(25, (SELECT id FROM product where name = 'Por' and group_created_id = 1) , 2),
(25, (SELECT id FROM product where name = 'Cytryna' and group_created_id = 1) , 1),
(25, (SELECT id FROM product where name = 'Śmietana' and group_created_id = 1) , 100),
(25, (SELECT id FROM product where name = 'Koperek' and group_created_id = 1) , 100),
(25, (SELECT id FROM product where name = 'Mąka pszenna' and group_created_id = 1) , 0.04),
(25, (SELECT id FROM product where name = 'Czosnek' and group_created_id = 1 ), 1),
-- Omlet z kurczakiem i brokułami
(26, (SELECT id FROM product where name = 'Brokuł' and group_created_id = 1) , 0.5),
(26, (SELECT id FROM product where name = 'Filet z kurczaka' and group_created_id = 1) , 0.2),
(26, (SELECT id FROM product where name = 'Olej oliwkowy' and group_created_id = 1) , 10),
(26, (SELECT id FROM product where name = 'Jaja' and group_created_id = 1), 10),
(26, (SELECT id FROM product where name = 'Masło' and group_created_id = 1) , 20),
-- Zrazy kasztelańskie
(27, (SELECT id FROM product where name = 'Wołowina' and group_created_id = 1) , 1),
(27, (SELECT id FROM product where name = 'Olej rzepakowy' and group_created_id = 1) , 50),
(27, (SELECT id FROM product where name = 'Mąka pszenna' and group_created_id = 1) , 0.02),
(27, (SELECT id FROM product where name = 'Śmietana' and group_created_id = 1) , 20),
(27, (SELECT id FROM product where name = 'Cebula' and group_created_id = 1) , 0.2),
(27, (SELECT id FROM product where name = 'Musztarda' and group_created_id = 1 ), 50),
(27, (SELECT id FROM product where name = 'Majeranek' and group_created_id = 1) , 10),
(27, (SELECT id FROM product where name = 'Suszone podgrzybki' and group_created_id = 1) , 20),
(27, (SELECT id FROM product where name = 'Chleb żytni' and group_created_id = 1) , 100),
-- Pulpety z indyka w sosie pomidorowym
(28, (SELECT id FROM product where name = 'Jaja' and group_created_id = 1) , 1),
(28, (SELECT id FROM product where name = 'Przyprawa do kurczaka' and group_created_id = 1) , 20),
(28, (SELECT id FROM product where name = 'Czosnek' and group_created_id = 1) , 1),
(28, (SELECT id FROM product where name = 'Cebula' and group_created_id = 1) , 0.1),
(28, (SELECT id FROM product where name = 'Olej oliwkowy' and group_created_id = 1) , 15),
(28, (SELECT id FROM product where name = 'Pomidory w puszce' and group_created_id = 1) , 200),
(28, (SELECT id FROM product where name = 'Mięso mielone z indyka' and group_created_id = 1) , 0.4),
(28, (SELECT id FROM product where name = 'Bułka tarta' and group_created_id = 1) , 60),
-- Nuggetsy
(29, (SELECT id FROM product where name = 'Filet z piersi kurczaka' and group_created_id = 1) , 0.2),
(29, (SELECT id FROM product where name = 'Kefir' and group_created_id = 1) , 0.25),
(29, (SELECT id FROM product where name = 'Jaja' and group_created_id = 1) , 1),
(29, (SELECT id FROM product where name = 'Mąka pszenna' and group_created_id = 1) , 0.04),
(29, (SELECT id FROM product where name = 'Olej rzepakowy' and group_created_id = 1) , 25),
(29, (SELECT id FROM product where name = 'Płatki kukurydziane' and group_created_id = 1) , 60),
-- Placki z cukini
(30, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 1),
(30, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.04),
(30, (SELECT id FROM product WHERE name = 'Koperek' AND group_created_id = 1), 10),
(30, (SELECT id FROM product WHERE name = 'Cukinia' AND group_created_id = 1), 0.25),
-- Zapiekanka ziemniaczana z cukinią
(31, (SELECT id FROM product WHERE name = 'Cukinia' AND group_created_id = 1), 0.6),
(31, (SELECT id FROM product WHERE name = 'Ziemniaki' AND group_created_id = 1), 0.45),
(31, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 3),
(31, (SELECT id FROM product WHERE name = 'Mleko' AND group_created_id = 1), 0.125),
(31, (SELECT id FROM product WHERE name = 'Śmietana' AND group_created_id = 1), 125),
(31, (SELECT id FROM product WHERE name = 'Ser żółty' AND group_created_id = 1), 150),
-- Dorsz w pomidorach z tymiankiem
(33, (SELECT id FROM product WHERE name = 'Filet z dorsza' AND group_created_id = 1), 0.3),
(33, (SELECT id FROM product WHERE name = 'Tymianek' AND group_created_id = 1), 20),
(33, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.1),
(33, (SELECT id FROM product WHERE name = 'Pomidory w puszce' AND group_created_id = 1), 100),
(33, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 10),
(33, (SELECT id FROM product WHERE name = 'Sos sojowy' AND group_created_id = 1), 20),
(33, (SELECT id FROM product WHERE name = 'Pomidory koktajlowe' AND group_created_id = 1), 0.25),
-- Łosoś w kremowym sosie ze sparagami
(34, (SELECT id FROM product WHERE name = 'Filet łososia' AND group_created_id = 1), 0.6),
(34, (SELECT id FROM product WHERE name = 'Czosnek' AND group_created_id = 1), 0.5),
(34, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.01),
(34, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.05),
(34, (SELECT id FROM product WHERE name = 'Śmietanka' AND group_created_id = 1), 120),
(34, (SELECT id FROM product WHERE name = 'Koperek' AND group_created_id = 1), 20),
(34, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 0.01),
(34, (SELECT id FROM product WHERE name = 'Szparagi' AND group_created_id = 1), 0.3),
(34, (SELECT id FROM product WHERE name = 'Bulion warzywny' AND group_created_id = 1), 200),
-- Kurczak w orzechowej panierce
(35, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 1),
(35, (SELECT id FROM product WHERE name = 'Filet z piersi kurczaka' AND group_created_id = 1), 0.3),
(35, (SELECT id FROM product WHERE name = 'Pietruszka' AND group_created_id = 1), 20),
(35, (SELECT id FROM product WHERE name = 'Bułka tarta' AND group_created_id = 1), 20),
(35, (SELECT id FROM product WHERE name = 'Parmezan' AND group_created_id = 1), 20),
(35, (SELECT id FROM product WHERE name = 'Orzechy włoskie' AND group_created_id = 1), 50),
-- Roladki z tortilii
(36, (SELECT id FROM product WHERE name = 'Pomidor' AND group_created_id = 1), 0.2),
(36, (SELECT id FROM product WHERE name = 'Musztarda' AND group_created_id = 1), 10),
(36, (SELECT id FROM product WHERE name = 'Szczypiorek' AND group_created_id = 1), 10),
(36, (SELECT id FROM product WHERE name = 'Szynka' AND group_created_id = 1), 100),
(36, (SELECT id FROM product WHERE name = 'Majonez' AND group_created_id = 1), 20),
(36, (SELECT id FROM product WHERE name = 'Tortilla' AND group_created_id = 1), 2),
(36, (SELECT id FROM product WHERE name = 'Sałata' AND group_created_id = 1), 100),
(36, (SELECT id FROM product WHERE name = 'Serek śmietankowy' AND group_created_id = 1), 200),
-- Pieczone talarki z cukini
(37, (SELECT id FROM product WHERE name = 'Cukinia' AND group_created_id = 1), 0.3),
(37, (SELECT id FROM product WHERE name = 'Parmezan' AND group_created_id = 1), 60),
(37, (SELECT id FROM product WHERE name = 'Bułka tarta' AND group_created_id = 1), 20),
(37, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 30),
(37, (SELECT id FROM product WHERE name = 'Oregano' AND group_created_id = 1), 10),
(37, (SELECT id FROM product WHERE name = 'Jogurt grecki' AND group_created_id = 1), 40),
(37, (SELECT id FROM product WHERE name = 'Migdały' AND group_created_id = 1), 40),
-- Pieczarki w serowym cieście
(38, (SELECT id FROM product WHERE name = 'Bułka tarta' AND group_created_id = 1), 50),
(38, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.03),
(38, (SELECT id FROM product WHERE name = 'Oregano' AND group_created_id = 1), 10),
(38, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 40),
(38, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 3),
(38, (SELECT id FROM product WHERE name = 'Pieczarki' AND group_created_id = 1), 0.4),
(38, (SELECT id FROM product WHERE name = 'Ser żółty' AND group_created_id = 1), 50),
-- Carpaccio z łososia
(39, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 45),
(39, (SELECT id FROM product WHERE name = 'Bazylia' AND group_created_id = 1), 45),
(39, (SELECT id FROM product WHERE name = 'Łosoś wędzony' AND group_created_id = 1), 150),
(39, (SELECT id FROM product WHERE name = 'Sok cytrynowy' AND group_created_id = 1), 20),
(39, (SELECT id FROM product WHERE name = 'Ocet balsamiczny' AND group_created_id = 1), 10),
-- Pieczarki w cieście francuskim
(40, (SELECT id FROM product WHERE name = 'Pieczarki' AND group_created_id = 1), 0.4),
(40, (SELECT id FROM product WHERE name = 'Masło' AND group_created_id = 1), 10),
(40, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.2),
(40, (SELECT id FROM product WHERE name = 'Ciasto francuskie' AND group_created_id = 1), 350),
-- Souvlaki
(41, (SELECT id FROM product WHERE name = 'Czosnek' AND group_created_id = 1), 1),
(41, (SELECT id FROM product WHERE name = 'Sok cytrynowy' AND group_created_id = 1), 50),
(41, (SELECT id FROM product WHERE name = 'Filet z piersi kurczaka' AND group_created_id = 1), 0.4),
(41, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 60),
-- Surówka z białej kapusty
(42, (SELECT id FROM product WHERE name = 'Cukier' AND group_created_id = 1), 100),
(42, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.1),
(42, (SELECT id FROM product WHERE name = 'Marchew' AND group_created_id = 1), 0.2),
(42, (SELECT id FROM product WHERE name = 'Olej rzepakowy' AND group_created_id = 1), 100),
(42, (SELECT id FROM product WHERE name = 'Ocet balsamiczny' AND group_created_id = 1), 100),
(42, (SELECT id FROM product WHERE name = 'Kapusta' AND group_created_id = 1), 0.5),
-- Śledzie pod pierzynką z majonezem
(43, (SELECT id FROM product WHERE name = 'Ziemniaki' AND group_created_id = 1), 0.3),
(43, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.1),
(43, (SELECT id FROM product WHERE name = 'Majonez' AND group_created_id = 1), 200),
(43, (SELECT id FROM product WHERE name = 'Pietruszka' AND group_created_id = 1), 50),
(43, (SELECT id FROM product WHERE name = 'Marchew' AND group_created_id = 1), 0.15),
(43, (SELECT id FROM product WHERE name = 'Ser żółty' AND group_created_id = 1), 150),
(43, (SELECT id FROM product WHERE name = 'Filety śledziowe w oleju' AND group_created_id = 1), 400),
(43, (SELECT id FROM product WHERE name = 'Korniszony' AND group_created_id = 1), 75),
(43, (SELECT id FROM product WHERE name = 'Sos czosnkowy' AND group_created_id = 1), 75),
-- Tortilla z paprykowym nadzieniem
(44, (SELECT id FROM product WHERE name = 'Tortilla' AND group_created_id = 1), 4),
(44, (SELECT id FROM product WHERE name = 'Bulion warzywny (kostka)' AND group_created_id = 1), 1),
(44, (SELECT id FROM product WHERE name = 'Cebula' AND group_created_id = 1), 0.1),
(44, (SELECT id FROM product WHERE name = 'Kolendra' AND group_created_id = 1), 40),
(44, (SELECT id FROM product WHERE name = 'Czosnek' AND group_created_id = 1), 1),
(44, (SELECT id FROM product WHERE name = 'Olej rzepakowy' AND group_created_id = 1), 60),
(44, (SELECT id FROM product WHERE name = 'Kmin rzymski' AND group_created_id = 1), 40),
(44, (SELECT id FROM product WHERE name = 'Papryka czerwona' AND group_created_id = 1), 0.35),
(44, (SELECT id FROM product WHERE name = 'Papryka żółta' AND group_created_id = 1), 0.35),
(44, (SELECT id FROM product WHERE name = 'Papryka zielona' AND group_created_id = 1), 0.2),
(44, (SELECT id FROM product WHERE name = 'Ser Cheddar' AND group_created_id = 1), 50),
(44, (SELECT id FROM product WHERE name = 'Przecier pomidorowy' AND group_created_id = 1), 40),
-- Owoce pod pierzynką
(45, (SELECT id FROM product WHERE name = 'Jabłko' AND group_created_id = 1), 0.2),
(45, (SELECT id FROM product WHERE name = 'Gruszka' AND group_created_id = 1), 0.35),
(45, (SELECT id FROM product WHERE name = 'Masło' AND group_created_id = 1), 30),
(45, (SELECT id FROM product WHERE name = 'Miód' AND group_created_id = 1), 20),
(45, (SELECT id FROM product WHERE name = 'Migdały' AND group_created_id = 1), 30),
(45, (SELECT id FROM product WHERE name = 'Śliwka' AND group_created_id = 1), 0.1),
(45, (SELECT id FROM product WHERE name = 'Płatki owsiane' AND group_created_id = 1), 70),
-- Wegańskie naleśniki bez jajek
(46, (SELECT id FROM product WHERE name = 'Olej rzepakowy' AND group_created_id = 1), 20),
(46, (SELECT id FROM product WHERE name = 'Mleko ryżowe' AND group_created_id = 1), 375),
(46, (SELECT id FROM product WHERE name = 'Mąka gryczana' AND group_created_id = 1), 0.2),
(46, (SELECT id FROM product WHERE name = 'Mąka ziemniaczana' AND group_created_id = 1), 40),
(46, (SELECT id FROM product WHERE name = 'Otręby pszenne' AND group_created_id = 1), 80),
(46, (SELECT id FROM product WHERE name = 'Cukier' AND group_created_id = 1), 20),
-- Bruschetta z marynowanymi pomidorami
(47, (SELECT id FROM product WHERE name = 'Czosnek' AND group_created_id = 1), 1),
(47, (SELECT id FROM product WHERE name = 'Ocet balsamiczny' AND group_created_id = 1), 10),
(47, (SELECT id FROM product WHERE name = 'Pomidory cherry' AND group_created_id = 1), 0.3),
(47, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 30),
(47, (SELECT id FROM product WHERE name = 'Bagietka' AND group_created_id = 1), 1),
(47, (SELECT id FROM product WHERE name = 'Bazylia pęczek' AND group_created_id = 1), 1),
(47, (SELECT id FROM product WHERE name = 'Sos sałatkowy włoski' AND group_created_id = 1), 1),
-- Ryż na napoju kokosowym z mango i bananem
(48, (SELECT id FROM product where name = 'Limonka' and group_created_id = 1) , 2),
(48, (SELECT id FROM product where name = 'Banan' and group_created_id = 1) , 1),
(48, (SELECT id FROM product where name = 'Mango' and group_created_id = 1) , 1),
(48, (SELECT id FROM product where name = 'Cynamon' and group_created_id = 1) , 20),
(48, (SELECT id FROM product where name = 'Rodzynki' and group_created_id = 1 ), 50),
(48, (SELECT id FROM product where name = 'Syrop klonowy' and group_created_id = 1) , 160),
(48, (SELECT id FROM product where name = 'Napój kokosowy' and group_created_id = 1 ), 300),
(48, (SELECT id FROM product where name = 'Orzechy ziemne' and group_created_id = 1 ), 50),
(48, (SELECT id FROM product where name = 'Ryż jaśminowy' and group_created_id = 1) , 0.2),
-- Jaglanka z cynamonem
(49, (SELECT id FROM product where name = 'Banan' and group_created_id = 1) , 1),
(49, (SELECT id FROM product where name = 'Cukier' and group_created_id = 1) , 20),
(49, (SELECT id FROM product where name = 'Cynamon' and group_created_id = 1) , 5),
(49, (SELECT id FROM product where name = 'Jabłko' and group_created_id = 1) , 0.15),
(49, (SELECT id FROM product where name = 'Kasza jaglana' and group_created_id = 1) , 100),
(49, (SELECT id FROM product where name = 'Wiórki kokosowe' and group_created_id = 1) , 20),
(49, (SELECT id FROM product where name = 'Mleko roślinne' and group_created_id = 1) , 0.4),
-- Cukinia w zalewie musztardowej
(50, (SELECT id FROM product where name = 'Cukinia' and group_created_id = 1) , 2),
(50, (SELECT id FROM product where name = 'Woda niegazowana' and group_created_id = 1) , 1),
(50, (SELECT id FROM product where name = 'Cukier' and group_created_id = 1) , 250),
(50, (SELECT id FROM product where name = 'Musztarda' and group_created_id = 1) , 50),
(50, (SELECT id FROM product where name = 'Koperek' and group_created_id = 1) , 5),
(50, (SELECT id FROM product where name = 'Ziele angielskie' and group_created_id = 1) , 5),
(50, (SELECT id FROM product where name = 'Ocet spirytusowy' and group_created_id = 1) , 300),
(50, (SELECT id FROM product where name = 'Liście laurowe' and group_created_id = 1) , 10),
-- Kurki z kaszą gryczaną
(51, (SELECT id FROM product where name = 'Czosnek suszony' and group_created_id = 1) , 5),
(51, (SELECT id FROM product where name = 'Pomidor' and group_created_id = 1) , 0.2),
(51, (SELECT id FROM product where name = 'Kurki' and group_created_id = 1) , 300),
(51, (SELECT id FROM product where name = 'Szpinak' and group_created_id = 1) , 100),
(51, (SELECT id FROM product where name = 'Kasza gryczana' and group_created_id = 1) , 0.1),
-- Sałatka z bobem i miętą
(52, (SELECT id FROM product WHERE name = 'Olej oliwkowy' AND group_created_id = 1), 30),
(52, (SELECT id FROM product WHERE name = 'Mięta' AND group_created_id = 1), 50),
(52, (SELECT id FROM product WHERE name = 'Czosnek' AND group_created_id = 1), 0.5),
(52, (SELECT id FROM product WHERE name = 'Cytryna' AND group_created_id = 1), 0.5),
(52, (SELECT id FROM product WHERE name = 'Rukkola' AND group_created_id = 1), 50),
(52, (SELECT id FROM product WHERE name = 'Bób' AND group_created_id = 1), 300),
-- Domowe ciastka francuskie z cukrem
(17, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.25),
(17, (SELECT id FROM product WHERE name = 'Masło' AND group_created_id = 1), 180),
(17, (SELECT id FROM product WHERE name = 'Cukier' AND group_created_id = 1), 20),
(17, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 1),
(17, (SELECT id FROM product WHERE name = 'Śmietana' AND group_created_id = 1), 100),
-- Babka pomarańczowa
(18, (SELECT id FROM product WHERE name = 'Masło' AND group_created_id = 1), 200),
(18, (SELECT id FROM product WHERE name = 'Cukier' AND group_created_id = 1), 200),
(18, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 5),
(18, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.18),
(18, (SELECT id FROM product WHERE name = 'Mąka ziemniaczana' AND group_created_id = 1), 80),
(18, (SELECT id FROM product WHERE name = 'Proszek do pieczenia' AND group_created_id = 1), 20),
(18, (SELECT id FROM product WHERE name = 'Pomarańcza' AND group_created_id = 1), 0.2),
(18, (SELECT id FROM product WHERE name = 'Ekstrakt pomarańczowy' AND group_created_id = 1), 10),
(18, (SELECT id FROM product WHERE name = 'Cukier puder' AND group_created_id = 1), 100),
-- Ciasto czekoladowe z bananami
(19, (SELECT id FROM product WHERE name = 'Masło' AND group_created_id = 1), 100),
(19, (SELECT id FROM product WHERE name = 'Mleko' AND group_created_id = 1), 0.1),
(19, (SELECT id FROM product WHERE name = 'Czekolada mleczna' AND group_created_id = 1), 80),
(19, (SELECT id FROM product WHERE name = 'Mąka pszenna' AND group_created_id = 1), 0.23),
(19, (SELECT id FROM product WHERE name = 'Jaja' AND group_created_id = 1), 3),
(19, (SELECT id FROM product WHERE name = 'Proszek do pieczenia' AND group_created_id = 1), 20),
(19, (SELECT id FROM product WHERE name = 'Kakao' AND group_created_id = 1), 100),
(19, (SELECT id FROM product WHERE name = 'Jogurt naturalny' AND group_created_id = 1), 80),
(19, (SELECT id FROM product WHERE name = 'Banan' AND group_created_id = 1), 5);


UPDATE recipe_ingredient SET qty = 170 WHERE recipe_id = 3 and ingredient_id = 79;