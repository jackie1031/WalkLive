USE walklive;

DROP TABLE IF EXISTS Trips, users, friend_requests;



CREATE TABLE Trips (tripId INT PRIMARY KEY, username VARCHAR(25), start_time VARCHAR(25),end_time VARCHAR(25), danger_zone INT, destination VARCHAR(25), coord_long DOUBLE(50,4),coord_lat DOUBLE(50,4), completed BOOLEAN);

CREATE TABLE users (username VARCHAR(25) PRIMARY KEY, password VARCHAR(25));

CREATE TABLE friend_requests(username VARCHAR(25) PRIMARY KEY);


INSERT INTO users (username, password ) VALUES ('michelle', '123123');

INSERT INTO Trips(tripId, username, start_time, end_time, danger_zone , destination, coord_long ,coord_lat , completed ) VALUES (1, 'Jackie Rules!', '060606','063606',3,'JHU Malone',3.2323,352.35235,1);