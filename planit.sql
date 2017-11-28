 -- Drop is a dangerous command
DROP DATABASE IF EXISTS planit;

CREATE DATABASE planit;

USE planit;


CREATE TABLE Users(
	userID INT(11) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL
);

CREATE TABLE Events(
	eventID INT(11) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    creator VARCHAR(50) NOT NULL,
    userID INT(11) NOT NULL,
    url VARCHAR(50) NOT NULL,
    FOREIGN KEY fk3(userID) REFERENCES Users(userID)
);

CREATE TABLE joinedEvent(
	joinedEvent INT(11) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    joined VARCHAR(20) NOT NULL,
    eventID INT(11) NOT NULL,
    FOREIGN KEY fk1(eventID) REFERENCES Events(eventID)
);
CREATE TABLE invitedEmails(
	invitedEmail INT(11) PRIMARY KEY AUTO_INCREMENT,
    emails VARCHAR(50) NOT NULL,
    eventID INT(11) NOT NULL,
    FOREIGN KEY fk2(eventID) REFERENCES Events(eventID)
);
