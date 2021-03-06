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
    name VARCHAR(50) NOT NULL,
    creator VARCHAR(50) NOT NULL,
    userID INT(11) NOT NULL,
    url VARCHAR(50) NOT NULL,
    -- --REMOVE THESE
--     joinedEventID INT(11) NOT NULL,
--     invitedEmailID INT(11) NOT NULL,    
    duration INT(10) NOT NULL,
    isRecurring VARCHAR (20) NOT NULL,
    isPublic VARCHAR(20) NOT NULL,
    FOREIGN KEY fk1(userID) REFERENCES Users(userID)
    -- FOREIGN KEY fk4(joinedEventID) REFERENCES joinedEvent(joinedEventID)
-- 	FOREIGN KEY fk5(invitedEmailID) REFERENCES invitedEmails(invitedEmailID)
);

CREATE TABLE joinedEvent(
	joinedEventID INT(11) PRIMARY KEY AUTO_INCREMENT,
	userID INT(11) NOT NULL,
    eventID INT(11) NOT NULL,
    FOREIGN KEY fk2(eventID) REFERENCES Events(eventID),
    FOREIGN KEY fk3(userID) REFERENCES Users(userID)
);

CREATE TABLE invitedEmails(
	invitedEmailID INT(11) PRIMARY KEY AUTO_INCREMENT,
    emails VARCHAR(50) NOT NULL,
    eventID INT(11) NOT NULL,
    FOREIGN KEY fk4(eventID) REFERENCES Events(eventID)
);

CREATE TABLE availabilityIntervals(
	availabilityIntervalID INT(11) PRIMARY KEY AUTO_INCREMENT,
    start VARCHAR(50) NOT NULL,
    end VARCHAR(50) NOT NULL,
    eventID INT(11) NOT NULL,
    FOREIGN KEY fk5(eventID) REFERENCES Events(eventID)
);

CREATE TABLE availabilities(
	availabilityID INT(11) PRIMARY KEY AUTO_INCREMENT,
    userID INT(11) NOT NULL,
    start VARCHAR(50) NOT NULL,
    end VARCHAR(50) NOT NULL,
    eventID INT(11) NOT NULL,
	FOREIGN KEY fk6(userID) REFERENCES Users(userID),
    FOREIGN KEY fk7(eventID) REFERENCES Events(eventID)
);

