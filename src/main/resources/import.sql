INSERT INTO characters (id, name, race, ki) VALUES (100, 'Piccolo Local', 'Namekian', 3000);
INSERT INTO characters (id, name, race, ki) VALUES (101, 'Krillin Local', 'Humano', 1500);
ALTER TABLE characters ALTER COLUMN id RESTART WITH 102;