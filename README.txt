Aby projekt działał poprawnie należy stworzyc oraz zaimportować rekordy używając tych zapytań SQL.
Najlepszy efekt otrzymamy uzywajac pgAdmin(postgreSQL);

CREATE DATABASE roomiesdb;

CREATE TABLE RodzajPokoju (
    id_rodzaj SERIAL PRIMARY KEY,
    nazwa VARCHAR(255) NOT NULL,
    wyposazenie VARCHAR(255),
    ilosc_metrow INT
);

CREATE TABLE Lokalizacja (
    id_lokalizacji SERIAL PRIMARY KEY,
    nazwa_lokalizacji VARCHAR(255) NOT NULL,
    kraj VARCHAR(255) NOT NULL,
    miasto VARCHAR(255) NOT NULL
);

CREATE TABLE Klienci (
    id_klienta SERIAL PRIMARY KEY,
    imie VARCHAR(255) NOT NULL,
    nazwisko VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefon VARCHAR(20),
    ulica VARCHAR(255) NOT NULL,
    numer_budynku VARCHAR(10) NOT NULL,
    pesel VARCHAR(11) UNIQUE NOT NULL,
    czy_aktywny BOOLEAN DEFAULT TRUE,
    haslo VARCHAR(255) NOT NULL
);

CREATE TABLE Platnosci (
    id_platnosci SERIAL PRIMARY KEY,
    status VARCHAR(20) CHECK (status IN ('OCZEKUJACE', 'ANULOWANO', 'ZAPŁACONO')) NOT NULL,
    kwota DECIMAL(10, 2) NOT NULL
);

CREATE TABLE Pokoje (
    id_pokoju SERIAL PRIMARY KEY,
    id_rodzaju_pokoju INT NOT NULL REFERENCES RodzajPokoju(id_rodzaj),
    id_lokalizacji INT REFERENCES Lokalizacja(id_lokalizacji),
    dostepnosc BOOLEAN NOT NULL,
    nazwa VARCHAR(255) NOT NULL,
    cena_za_dobe DECIMAL(10,2) NOT NULL,
    sciezka_zdjecia VARCHAR(255)
);

CREATE TABLE Wynajem (
    id_wynajmu SERIAL PRIMARY KEY,
    id_pokoju INT REFERENCES Pokoje(id_pokoju) ON DELETE CASCADE,
    id_klienta INT REFERENCES Klienci(id_klienta) ON DELETE CASCADE,
    data_rozpoczecia DATE NOT NULL,
    data_zakoczenia DATE,
    id_platnosci INT REFERENCES Platnosci(id_platnosci)
);

INSERT INTO RodzajPokoju (nazwa, wyposazenie, ilosc_metrow) VALUES
('Jednoosobowy Ekonomiczny', 'Pojedyncze łóżko, biurko, szafa, dostęp do wspólnej łazienki', 10),
('Jednoosobowy Standard', 'Pojedyncze łóżko, biurko, szafa, TV, prywatna łazienka z prysznicem', 14),
('Dwuosobowy Standard (Twin)', 'Dwa pojedyncze łóżka, biurka, szafy, TV, prywatna łazienka', 18),
('Dwuosobowy Standard (Double)', 'Podwójne łóżko, biurko, szafa, TV, prywatna łazienka', 18),
('Apartament Junior', 'Podwójne łóżko, część wypoczynkowa, aneks kuchenny, łazienka, balkon', 35);

INSERT INTO Lokalizacja (nazwa_lokalizacji, kraj, miasto) VALUES
('Akademik "Słoneczny Brzeg"', 'Polska', 'Gdańsk'),
('Dom Studencki "Centrum"', 'Polska', 'Warszawa'),
('Campus "Politechnika" Bud. C', 'Polska', 'Wrocław');

INSERT INTO Klienci (imie, nazwisko, email, telefon, ulica, numer_budynku, pesel, czy_aktywny, haslo) VALUES
('Alicja', 'Nowicka', 'alicja.nowicka@example.com', '501123456', 'Akademicka', '10A', '98011512345', TRUE, 'pass123'),
('Bartosz', 'Kowalczyk', 'b.kowalczyk@example.net', '602234567', 'Politechniczna', '3C/2', '99070154321', TRUE, 'bartek321'),
('Celina', 'Zielińska', 'celina.z@example.org', NULL, 'Uniwersytecka', '5', '00220567890', TRUE, 'hasloCeliny'),
('Damian', 'Woźniak', 'damian.w@student.com', '703345678', 'Mieszkaniowa', '115', '97122078901', FALSE, 'damian1');

INSERT INTO Pokoje (id_rodzaju_pokoju, id_lokalizacji, dostepnosc, nazwa, cena_za_dobe, sciezka_zdjecia) VALUES
(1, 1, TRUE, 'Pokój 101 - Ekonomiczny', 75.00, '/images/pokoje/zdjecie1.jpg'),
(2, 1, TRUE, 'Pokój 102 - Standard', 110.00, '/images/pokoje/zdjecie2.jpg'),
(3, 2, TRUE, 'Pokój 215 - Twin', 150.00, '/images/pokoje/zdjecie3.jpg'),
(4, 2, TRUE, 'Pokój 216 - Double', 160.00, '/images/pokoje/zdjecie4.jpg'),
(5, 3, TRUE, 'Apartament 301 - Junior', 280.00, '/images/pokoje/zdjecie5.jpg'),
(2, 3, TRUE, 'Pokój 11 - Standard Solo', 120.00, '/images/pokoje/zdjecie6.jpg'),
(1, 1, TRUE, 'Pokój 103 - Ekonomiczny Plus', 80.00, '/images/pokoje/zdjecie7.jpg'),
(3, 2, TRUE, 'Pokój 217 - Twin Comfort', 155.00, '/images/pokoje/zdjecie8.jpg');

INSERT INTO Platnosci (status, kwota) VALUES ('ZAPŁACONO', 330.00);
INSERT INTO Wynajem (id_pokoju, id_klienta, data_rozpoczecia, data_zakoczenia, id_platnosci) VALUES
(2, 1, '2025-05-10', '2025-05-13', currval('platnosci_id_platnosci_seq'));
UPDATE Pokoje SET dostepnosc = FALSE WHERE id_pokoju = 2;

INSERT INTO Platnosci (status, kwota) VALUES ('OCZEKUJACE', 750.00);
INSERT INTO Wynajem (id_pokoju, id_klienta, data_rozpoczecia, data_zakoczenia, id_platnosci) VALUES
(3, 2, '2025-06-15', '2025-06-20', currval('platnosci_id_platnosci_seq'));
UPDATE Pokoje SET dostepnosc = FALSE WHERE id_pokoju = 3;

INSERT INTO Platnosci (status, kwota) VALUES ('OCZEKUJACE', 560.00);
INSERT INTO Wynajem (id_pokoju, id_klienta, data_rozpoczecia, data_zakoczenia, id_platnosci) VALUES
(5, 1, '2025-07-01', '2025-07-03', currval('platnosci_id_platnosci_seq'));