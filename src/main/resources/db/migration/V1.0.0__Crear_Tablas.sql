CREATE TABLE z_characters (
    id BIGINT NOT NULL,
    name VARCHAR(255),
    race VARCHAR(255),
    ki INT,
    maxKi VARCHAR(255),
    image VARCHAR(255),
    affiliation VARCHAR(255),
    PRIMARY KEY (id)
);
-- Agrega la secuencia si usas @GeneratedValue
CREATE SEQUENCE z_characters_SEQ START WITH 1 INCREMENT BY 50;