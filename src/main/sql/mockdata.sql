INSERT INTO Users (name, email) VALUES ('aa', 'aa@gmail.com'), ('ll', 'll@gmail.com'), ('dd', 'dd@gmail.com');



INSERT INTO UsersTokens (token, userId, creationDate)
VALUES (uuid_generate_v4(), 1, now()),(uuid_generate_v4(), 2, now()),(uuid_generate_v4(), 3, now());


DELETE FROM userstokens;


--'f52129ca-ccf1-42cc-a363-fdc89f71901b'::uuid
SELECT * FROM pg_extension WHERE extname = 'uuid-ossp';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
