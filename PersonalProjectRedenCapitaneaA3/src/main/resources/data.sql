INSERT INTO song (id, title, artist_name, album_name, duration, rank)
VALUES
    (1, 'Disease', 'Lady Gaga', NULL, 230, 1),
    (2, 'APT.', 'ROSÉ, Bruno Mars', NULL, 170, 2),
    (3, 'Taste', 'Sabrina Carpenter', 'Short n'' Sweet', 157, 3),
    (4, 'BIRDS OF A FEATHER', 'Billie Eilish', 'HIT ME HARD AND SOFT', 210, 4),
    (5, 'Timeless (with Playboi Carti)', 'The Weeknd, Playboi Carti', 'Timeless', 256, 5),
    (6, 'Good Luck, Babe!', 'Chappell Roan', NULL, 218, 6),
    (7, 'Die With A Smile', 'Lady Gaga, Bruno Mars', NULL, 252, 7),
    (8, 'Please Please Please', 'Sabrina Carpenter', NULL, 186, 8),
    (9, 'That''s So True', 'Gracie Abrams', 'The Secret of Us (Deluxe)', 166, 9),
    (10, 'Diet Pepsi', 'Addison Rae', NULL, 170, 10),
    (11, 'Guess featuring billie eilish', 'Charli xcx, Billie Eilish', NULL, 143, 11),
    (12, 'It''s ok I''m ok', 'Tate McRae', NULL, 157, 12),
    (13, 'A Bar Song (Tipsy)', 'Shaboozey', NULL, 171, 13),
    (14, 'WILDFLOWER', 'Billie Eilish', 'HIT ME HARD AND SOFT', 261, 14),
    (15, 'HOT TO GO!', 'Chappell Roan', 'The Rise and Fall of a Midwest Princess', 185, 15),
    (16, 'Mantra', 'JENNIE', NULL, 137, 16),
    (17, 'I Had Some Help (Feat. Morgan Wallen)', 'Post Malone, Morgan Wallen', 'I Had Some Help', 178, 17),
    (18, 'Sailor Song', 'Gigi Perez', NULL, 212, 18),
    (19, 'MILLION DOLLAR BABY', 'Tommy Richman', NULL, 155, 19),
    (20, 'São Paulo (feat. Anitta)', 'The Weeknd, Anitta', 'São Paulo', 302, 20);
    
    
INSERT INTO sec_role (roleName)
VALUES ('ROLE_USER');


INSERT INTO sec_role (roleName)
VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password)
VALUES ('admin', '$2y$10$E8KBy9m/VeoBE.yulsdnJeLeCulBkrPrKmzBYQh0c7FCZZZAunEIy');

INSERT INTO user_role (userId, roleId)
VALUES (1, 1);

INSERT INTO user_role (userId, roleId)
VALUES (1, 2);
