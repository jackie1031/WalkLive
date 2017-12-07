USE walklive;

DROP TABLE IF EXISTS Trips, users, friend_requests;



CREATE TABLE Trips (tripId INT PRIMARY KEY, start_time VARCHAR(25));
CREATE TABLE users (username VARCHAR(25) PRIMARY KEY, password VARCHAR(25));

CREATE TABLE friend_requests(username VARCHAR(25) PRIMARY KEY);

INSERT INTO Trips(tripId, start_time) VALUES (1, 'Jack London');
