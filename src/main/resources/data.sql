INSERT INTO user_group VALUES (1);

SELECT setval('user_group_id_seq', 1, true);

INSERT INTO app_user(email, username, password, role, group_id, is_active) VALUES
('marianamariana1509@gmail.com', 'maryana', '$2a$12$6C3AdGX37TU14oycAAfegOEUKhVpVYZkD5lL4yG4Ke8D2dlIpmxQ.',
 'ROLE_ADMIN', 1, true);

INSERT INTO app_user(email, username, password, role, group_id, is_active) VALUES
('marianamartyniuk2001@gmail.com', 'maryana2', '$2a$12$h/P7FJedQLkojas2qPP19eyTHnuQlSHDwzXtRBrubdAEhdEjx1rfK',
'ROLE_USER', 1, true);


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
('Jogurt', 2, 1, 'L'),
('Kefir', 2, 1, 'L'),
('Maślanka', 2, 1, 'L'),
('Śmietanka', 2, 1, 'GR'),
('Śmietana', 2, 1, 'L'),
('Ser', 2, 1, 'GR'),

('Kajzerki', 3, 1, 'SZT'),
('Chleb żytni', 3, 1, 'GR'),

('Pierś kurczaka', 4, 1, 'KG'),
('Boczek', 4, 1, 'KG'),
('Wędlina', 4, 1, 'KG'),

('Banan', 5, 1, 'SZT'),
('Jabłko', 5, 1, 'GR'),
('Pomarańcza', 5, 1, 'GR'),

('Ogórek', 6, 1, 'KG'),
('Pomidor', 6, 1, 'KG'),
('Ziemniaki', 6, 1, 'KG'),
('Marchew', 6, 1, 'KG'),
('Cebula', 6, 1, 'KG'),

('Mąka', 7, 1, 'KG'),
('Ryż', 7, 1, 'KG'),
('Owsianka', 7, 1, 'KG'),
('Spaghetti', 7, 1, 'KG'),


('Dorsz', 8, 1, 'KG'),
('Karp', 8, 1, 'KG'),
('Łosoś', 8, 1, 'KG'),
('Krewetki', 8, 1, 'KG'),
('Ośmiornica', 8, 1, 'KG'),


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
('Sok pomarańczowy', 11, 1, 'L'),
('Piwo ciemne', 11, 1, 'L'),
('Piwo jasne', 11, 1, 'L');


-- miód wielokwiatowy