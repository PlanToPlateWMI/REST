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
('Ryby i owoce morza'),
('Przyprawy i zioła'),
('Bakalie'),
('Napoje'),
('Konserwy'),
('Inne');



INSERT INTO product (name, category_id, group_created_id, unit) VALUES
('Mleko', 2, 1, 'L'),
('Jogurt', 2, 1, 'L'),
('Kefir', 2, 1, 'L'),
('Maślanka', 2, 1, 'L'),
('Śmietanka', 2, 1, 'G'),
('Śmietana', 2, 1, 'L'),
('Ser', 2, 1, 'G'),

('Kajzerki', 3, 1, 'SZTUKA'),
('Chleb żytni', 3, 1, 'G'),

('Pierś kurczaka', 4, 1, 'KG'),
('Boczek', 4, 1, 'KG'),
('Wędlina', 4, 1, 'KG'),

('Banan', 5, 1, 'SZTUKA'),
('Jabłko', 5, 1, 'G'),
('Pomarańcza', 5, 1, 'G');


INSERT INTO product (name, category_id, group_created_id) VALUES

('Ogórek', 6, 1),
('Pomidor', 6, 1),
('Ziemniaki', 6, 1),
('Marchew', 6, 1),
('Cebula', 6, 1),

('Mąka', 7, 1),
('Ryż', 7, 1),
('Owsianka', 7, 1),
('Spaghetti', 7, 1),


('Dorsz', 8, 1),
('Karp', 8, 1),
('Łosoś', 8, 1),
('Krewetki', 8, 1),
('Ośmiornica', 8, 1),


('Pieprz czerwony mielony', 9, 1),
('Sól', 9, 1),
('Papryka ostra melona', 9, 1),
('Papryka słodka melona', 9, 1),
('Cukier', 9, 1),


('Orzechy włoskie', 10, 1),
('Nerkowiec', 10, 1),
('Orzechy ziemne', 10, 1),
('Rodzynki', 10, 1),
('Morele suszone', 10, 1),

('Woda niegazowana', 11, 1),
('Woda gazowana', 11, 1),
('Sok pomarańczowy', 11, 1),
('Piwo ciemne', 11, 1),
('Piwo jasne', 11, 1);


-- miód wielokwiatowy