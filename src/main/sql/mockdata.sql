INSERT INTO Users (name, email) VALUES ('aa', 'aa@gmail.com'), ('ll', 'll@gmail.com'), ('dd', 'dd@gmail.com');

drop table users cascade ;

INSERT INTO UsersTokens (token, userId, creationDate)
VALUES ('f52129ca-ccf1-42cc-a363-fdc89f71901b', 1, now()),('f52129ca-ccf1-4222-a363-fdc89f71901b', 2, now()),('f52129ca-ccf1-cccc-a363-fdc89f71901b', 3, now());
--VALUES (uuid_generate_v4(), 1, now()),(uuid_generate_v4(), 2, now()),(uuid_generate_v4(), 3, now());

delete from boards where name='board2';
DELETE FROM userstokens;
 drop table userstokens;
SELECT u.* FROM Users u JOIN UsersTokens ut ON u.id = ut.userId WHERE ut.token = 'f52129ca-ccf1-4222-a363-fdc89f71901b';
--'f52129ca-ccf1-42cc-a363-fdc89f71901b'::uuid
SELECT * FROM pg_extension WHERE extname = 'uuid-ossp';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO Users (name,email) VALUES ('sergio', 'aa@bb.com');
delete from users where name='sergio';


insert into boards (name, description)values ('board1','some description1');

insert into usersboards values (1,1),(2,1),(3,1);


delete from boards
