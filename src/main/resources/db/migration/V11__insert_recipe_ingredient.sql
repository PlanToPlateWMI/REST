INSERT INTO product (name, category_id, group_created_id, unit) VALUES
('Kawa', 13, 1, 'GR'), -- 103
('Cynamon', 9, 1, 'GR'), -- 104
('Gałka muszkatołowa', 9, 1, 'GR'), -- 105
('Bita śmietanka', 2, 1, 'ML'), -- 106
('Imbir suszony', 9, 1, 'GR'), -- 107
('Bulion warzywny', 9, 1, 'ML'), -- 108
('Dynia', 6, 1, 'KG'), -- 109
('Czosnek suszony', 9, 1, 'GR'), -- 110
('Curry', 9, 1, 'GR'), -- 111
('Kurkuma', 9, 1, 'GR'), -- 112
('Olej oliwkowy', 9, 1,'ML'), --113
('Bazylia', 9, 1,'GR'), --114
('Biała czekolada', 2, 1,'GR'), --115
('Matcha', 9,1, 'GR'), --116
('Mielona wołowina', 4, 1,'KG'), --117
('Fasola', 6, 1,'KG'), --118
('Papryka czerwona', 6, 1,'KG'), -- 119
('Oregano', 9, 1,'GR'), --120
('Kmin rzymski', 9, 1,'GR'), --121
('Jagody', 5, 1,'GR'), --122
('Awokado', 6,1, 'SZT'), --123
('Ser kozi', 2, 1,'GR'), --124
('Ocet balsamiczny', 9,1, 'ML'), --125
('Miód', 9,1, 'GR'), --126
('Kalafior', 6, 1,'KG'), --127
('Pietruszka', 9, 1,'GR'), --128
('Czarnuszka', 9, 1,'GR'), --129
('Cytryna', 5, 1,'SZT'), --130
('Imbir', 9, 1,'GR'), --131
('Cynamon laska', 9, 1,'SZT'), --132
('Goździki', 9,1, 'GR'), --133
('Anyż gwiazdki', 9, 1,'GR'), --134
('Mango', 5, 1,'SZT'), -- 135
('Herbata czarna', 12, 1,'GR'), --136
('Dżem malinowy', 13,1, 'GR'), --137
('Arbuz', 5, 1,'KG'), --138
('Melisa', 9, 1,'GR'), -- 139
('Burak', 6,1, 'KG'), --140
('Koperek', 9, 1,'GR'), --141
('Pomidory w puszce', 12, 1,'GR'), --142
('Ciecierzyca konserwowa', 12,1, 'GR'), --143
('Soczewica czerwona', 6, 1,'GR'), --144
('Makaron', 10, 1,'GR'), --145
('Daktyle suszone', 5,1, 'GR'), --146
('Seler naciowy', 6,1, 'GR'), --147
('Przecier pomidorowy', 12, 1,'GR'), --148
('Rosół z kury (kostka)', 9, 1,'SZT'), --149
('Bulion mięsny', 9, 1, 'ML'), --150
('Ziele angielskie', 9, 1, 'GR'), --151
('Papryka słodka', 9, 1, 'GR'), --152
('Kiełbasa chorizo', 4, 1, 'GR'), --153
('Kolendra', 9, 1, 'GR'), --154
('Fasola czerwona z puszki', 12, 1, 'GR'), --155
('Podudzie z kurczaka', 4, 1, 'KG'), --156
('Boczek wędzony', 4, 1, 'GR'), --157
('Kukurydza z puszki', 12, 1, 'GR'), --158
('Płatki chili', 9, 1, 'GR');--159




UPDATE product SET name = 'Mąka pszenna' where name = 'Mąka';

UPDATE product SET  unit = 'KG' where name = 'Pomarańcza';
UPDATE product SET  unit = 'KG' where name = 'Jabłko';


UPDATE recipe SET steps = 'Krok 0: Ugotować fasolę &
Krok 1: Na dużej patelni, na oliwie zeszklić pokrojoną w kosteczkę cebulę, dodać czosnek, paprykę, kmin rzymski i oregano i
mieszając smażyć przez 1 minutę. Stopniowo dodawać zmieloną wołowinę. & Krok 2: Pomidory należy sparzyć, obrać,
pokroić na ćwiartki, usunąć pestki, miąższ pokroić w kosteczkę i dodać do mięsa. Wymieszać i doprawić cukrem,
pieprzem oraz szczyptą soli. & Krok 3: Przykryć i dusić przez około 20 na umiarkowanym ogniu, co jakiś czas
zamieszać. Dodać pokrojoną w kosteczkę paprykę. Gotować pod przykryciem przez około 10 minut, od czasu do czasu
zamieszać. Na koniec dodać fasolę i gotować pod przykryciem przez ok. 3 minuty. & Krok 4: Na koniec gotować potrawę
podlewając stopniowo wrzącym bulionem lub wodą, tak aby powstało trochę gęstego sosu. Można przygotować wcześniej
i odgrzewać. Krok 5: Najlepiej podawać razem z gotowanym ryżem lub makaronem.'
where title = 'Chili con carne' ;


insert into recipe_ingredient(recipe_id,ingredient_id, qty) values
-- Spice latte
(1, 1, 0.2),
(1, 103, 20),
(1, 85, 0.05),
(1, 106, 100),
(1, 107, 5),
(1, 104, 5),
(1, 105, 5),
-- zupa dyniowa
(2, 108 , 750),
(2, 109, 0.75),
(2, 47 , 0.25),
(2, 46, 0.3),
(2, 49, 0.1),
(2, 110, 5),
(2, 111, 5),
(2, 112, 10),
(2, 113, 30),
(2, 5, 125),
(2, 114, 10),
--matcha browne
(3, 56, 0.13),
(3, 12, 120),
(3, 115, 120),
(3, 116, 20),
(3, 79, 0.17),
(3, 100, 3),
-- chilli con carne
(4, 117, 0.5),
(4, 118, 0.2),
(4, 46, 0.3),
(4, 119, 0.2),
(4, 108, 100),
(4, 113, 20),
(4, 120, 5),
(4, 121, 5),
(4, 110, 5),
(4, 77, 5),
-- sałatka z serkiem kozim i awokado
(5, 122, 200),
(5, 123, 1),
(5, 124, 80),
(5, 125, 50),
(5, 126, 10),
(5, 114, 10),
-- pieczony kalafior
(6, 127 , 250),
(6, 113, 40),
(6, 128, 20),
(6, 112, 5),
(6, 129, 5),
-- Jesienna herbata
(7, 85, 0.6),
(7, 39, 0.2), 
(7, 38, 0.2), 
(7, 130, 1),
(7, 131, 20),
(7, 132, 1),
(7, 133, 10),
(7, 126, 10),
(7, 134, 5),
-- Mango lassi
(8, 135, 2), 
(8, 11, 250), 
(8, 126, 50),
-- Rozgrzewająca herbata z goździkami
(10, 136, 5),
(10, 133, 5),
(10, 39, 0.2),
(10, 137, 40),
-- Mrożona herbata – arbuzowe orzeźwienie
(11, 138, 1),
(11, 136, 5),
(11, 130, 1),
(11, 139, 20),
-- Chłodnik litewski
(12, 140, 0.2),
(12, 3, 0.5),
(12, 6, 200),
(12, 45, 0.15),
(12, 49, 0.1),
(12, 141, 50),
(12, 100, 1),
(12, 110, 10),
-- Zupa marokańska
(13, 142, 800),
(13, 143, 500),
(13, 144, 100),
(13, 49, 0.25),
(13, 145, 150),
(13, 100, 6),
(13, 101, 50),
(13, 146, 100),
(13, 147, 300),
(13, 148, 40), 
-- Barszcz czerwony
(14, 140, 0.8),
(14, 108, 800),
(14, 49, 0.04),
-- Pomidorowa z makaronem
(15, 142, 400),
(15, 145, 150),
(15, 49, 0.15),
(15, 149, 2), 
(15, 50, 2),
-- Hiszpańska zupa z czerwoną fasolą i kiełbasą chorizo
(16, 150, 1000),
(16, 151, 5),
(16, 49, 0.1),
(16, 46, 0.2),
(16, 47, 0.4),
(16, 121, 10),
(16, 153, 150),
(16, 154, 50),
(16, 155, 150),
(16, 156, 0.5),
(16, 157, 50), 
(16, 158, 150),
(16, 159, 20);


