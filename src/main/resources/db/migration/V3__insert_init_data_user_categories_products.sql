INSERT INTO user_group(name) VALUES ('admins');

-- moderator
INSERT INTO app_user(email, username, password, role, group_id, is_active) VALUES
('plantoplatemobileapp@gmail.com', 'plantoplate', '$2a$10$SjxWexEjOJzrFR3CrQFaRehmWM1S6YzyALcSkTPDCbyFDNpypKfFK',
 'ROLE_ADMIN', 1, true);


INSERT INTO category (category) VALUES
('Produkty własne'),
('Produkty mleczne'),
('Wypieki'),
('Produkty mięsne'),
('Owoce'),
('Warzywa'),
('Produkty sypkie'),
('Ryby'),
('Przyprawy'),
('Bakalie'),
('Napoje'),
('Konserwy'),
('Inne');



INSERT INTO product (name, category_id, group_created_id, unit) VALUES
('Mleko', 2, 1, 'L'),
('Jogurt', 2, 1, 'ML'),
('Kefir', 2, 1, 'L'),
('Maślanka', 2, 1, 'L'),
('Śmietanka', 2, 1, 'GR'),
('Śmietana', 2, 1, 'L'),
('Ser Gouda', 2, 1, 'GR'),
('Ser Mozzarella', 2, 1, 'GR'),
('Ser Cheddar', 2, 1, 'GR'),
('Jogurt do picia', 2, 1, 'ML'),
('Jogurt naturalny', 2, 1, 'ML'),
('Masło', 2, 1, 'GR'),
('Margaryna', 2, 1, 'GR'),
('Śmietanka do kawy', 2, 1, 'SZT'),


('Kajzerki', 3, 1, 'SZT'),
('Chleb żytni', 3, 1, 'GR'),
('Chleb pszenny', 3, 1, 'GR'),
('Chleb tostowy', 3, 1, 'GR'),
('Pączek', 3, 1, 'SZT'),
('Croissant', 3, 1, 'SZT'),
('Bułka tarta', 3, 1, 'GR'),

('Pierś kurczaka', 4, 1, 'KG'),
('Boczek', 4, 1, 'KG'),
('Wędlina', 4, 1, 'KG'),
('Podudzie z kurczaka', 4, 1, 'KG'),
('Kaczka', 4, 1, 'KG'),
('Mięso mielone wieprzowo-wołowe', 4, 1, 'KG'),
('Mięso mielone wołowe', 4, 1, 'KG'),
('Schab wieprzowy', 4, 1, 'KG'),
('Polędwiczki wieprzowe', 4, 1, 'KG'),
('Wołowina', 4, 1, 'KG'),
('Szynka', 4, 1, 'GR'),
('Salami', 4, 1, 'GR'),
('Kabanosy', 4, 1, 'GR'),
('Kiełbaski', 4, 1, 'GR'),
('Parówki', 4, 1, 'GR'),

('Banan', 5, 1, 'SZT'),
('Jabłko', 5, 1, 'GR'),
('Pomarańcza', 5, 1, 'GR'),
('Ananas', 5, 1, 'SZT'),
('Melon', 5, 1, 'SZT'),
('Cytryny', 5, 1, 'SZT'),
('Grapefruit', 5, 1, 'KG'),
('Gruszka', 5, 1, 'KG'),

('Ogórek', 6, 1, 'KG'),
('Pomidor', 6, 1, 'KG'),
('Ziemniaki', 6, 1, 'KG'),
('Marchew', 6, 1, 'KG'),
('Cebula', 6, 1, 'KG'),
('Czosnek', 6, 1, 'SZT'),
('Por', 6, 1, 'SZT'),
('Seler', 6, 1, 'KG'),
('Pieczarki', 6, 1, 'KG'),
('Kalafior', 6, 1, 'SZT'),
('Kapusta', 6, 1, 'SZT'),

('Mąka', 7, 1, 'KG'),
('Ryż basmati', 7, 1, 'KG'),
('Ryż biały', 7, 1, 'KG'),
('Ryż jaśminowy', 7, 1, 'KG'),
('Owsianka', 7, 1, 'KG'),
('Spaghetti', 7, 1, 'KG'),
('Farfalle', 7, 1, 'KG'),
('Kasza jęczmienna', 7, 1, 'KG'),
('Kasza gryczana', 7, 1, 'KG'),
('Kasza manna', 7, 1, 'KG'),
('Kasza owsiana', 7, 1, 'KG'),


('Dorsz', 8, 1, 'KG'),
('Karp', 8, 1, 'KG'),
('Łosoś wiedziony', 8, 1, 'GR'),
('Łosoś świeży', 8, 1, 'KG'),
('Krewetki', 8, 1, 'KG'),
('Ośmiornica', 8, 1, 'KG'),
('Filet śledzi', 8, 1, 'KG'),
('Kawior', 8, 1, 'GR'),


('Pieprz czerwony mielony', 9, 1, 'GR'),
('Sól', 9, 1, 'KG'),
('Papryka ostra melona', 9, 1, 'GR'),
('Papryka słodka melona', 9, 1, 'GR'),
('Cukier', 9, 1, 'KG'),


('Orzechy włoskie', 10, 1, 'GR'),
('Nerkowiec', 10, 1, 'GR'),
('Orzechy ziemne', 10, 1, 'GR'),
('Rodzynki', 10, 1, 'GR'),
('Morele suszone', 10, 1, 'GR'),

('Woda niegazowana', 11, 1, 'L'),
('Woda gazowana', 11, 1, 'L'),
('Woda mineralna', 11, 1, 'L'),
('Sok pomarańczowy', 11, 1, 'L'),
('Sok jabłkowy', 11, 1, 'L'),
('Nektar czarna porzeczka', 11, 1, 'L'),
('Coca-cola', 11, 1, 'L'),
('Pepsi', 11, 1, 'L'),
('Sprite', 11, 1, 'L'),
('7UP', 11, 1, 'L'),
('Fanta', 11, 1, 'L'),
('Mirinda', 11, 1, 'L'),
('Red bull', 11, 1, 'L'),
('Piwo ciemne', 11, 1, 'L'),
('Piwo jasne', 11, 1, 'L'),


('Jaja', 13, 1, 'SZT');


-- miód wielokwiatowy