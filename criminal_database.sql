CREATE DATABASE criminal_db;
USE criminal_db;

CREATE TABLE criminals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    face_encoding BLOB NOT NULL
);

INSERT INTO criminals (name, face_encoding) VALUES ('Aastha Sahu', 'face_data_1');
