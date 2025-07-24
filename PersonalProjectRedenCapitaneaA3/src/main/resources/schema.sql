CREATE TABLE users (
	id LONG PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE sec_role (
	roleId   LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
	roleName VARCHAR(30) NOT NULL UNIQUE
);


CREATE TABLE user_role
(
  id     LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,
  userId LONG NOT NULL,
  roleId LONG NOT NULL
);

ALTER TABLE user_role
  ADD CONSTRAINT user_role_uk UNIQUE (userId, roleId);

ALTER TABLE user_role
  ADD CONSTRAINT user_role_fk1 FOREIGN KEY (userId)
  REFERENCES users (id);
 
ALTER TABLE user_role
  ADD CONSTRAINT user_role_fk2 FOREIGN KEY (roleId)
  REFERENCES sec_role (roleId);


CREATE TABLE song (
	id LONG PRIMARY KEY AUTO_INCREMENT,
	title VARCHAR(255),
	artist_name VARCHAR(255),
	album_name VARCHAR(255),
	duration INT,
	rank INT
);

CREATE TABLE playlist (
	id LONG PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	description VARCHAR(255),
	creator_id LONG,
	creator_name VARCHAR(255),
	CONSTRAINT fk_user FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE playlist_song (
	playlist_id LONG,
	song_id LONG,
	PRIMARY KEY (playlist_id, song_id),
	FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE,
	FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
);

CREATE TABLE liked_songs (
	song_id LONG,
	user_id LONG,
	PRIMARY KEY (user_id, song_id),
	FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
	FOREIGN KEY (song_id) REFERENCES song(id) ON DELETE CASCADE
);