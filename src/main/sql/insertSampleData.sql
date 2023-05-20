begin; --transaction

do $$ --anonymous pgsql scope for variables
declare lastuserid integer;
declare lastboardid integer;
declare lastlistid integer;

begin --anonymous scope

insert into users(name, email) values
    ('Beatriz', 'beatriz@example.org')
    returning id into lastuserid;
insert into userstokens(token, userId, creationDate) values
    ('f52129ca-ccf1-42cc-a363-fdc89f71901b',lastuserid,'2023-02-23 18:30:00');

insert into boards(name, description) values
    ('Viagem na Europa', 'Reunir as ideias da viagem')
    returning id into lastboardid;

insert into usersboards(userid, boardid) values (lastuserid,lastboardid);

insert into cards(name, description, duedate, listid, boardid,cidx) values
    ('Viena','Austria',null,null,lastboardid,0),
    ('Roma','Italia',null,null,lastboardid,1),
    ('Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch','Pais de Gales',null,null,lastboardid,2);


insert into lists(name, boardid) values
    ('Reservas', lastboardid)
    returning id into lastlistid;

insert into lists(name, boardid) values
    ('ReservasExtra', lastboardid)

insert into cards(name, description, duedate, listid, boardid,cidx) values
    ('Avião','Comprar os bilhetes para todos','2023-04-15 12:30:00',lastlistid,lastboardid,0),
    ('Hotel','Reservar os quartos (para quantos?)','2023-05-01 12:00:00',lastlistid,lastboardid,1),
    ('Transportes','De e para o aeroporto','2023-06-09 23:59:59',lastlistid,lastboardid,2);
update lists set ncards = 3 where id = lastlistid;

insert into lists(name, boardid) values
    ('Sitos para comer', lastboardid),
    ('Sitos para visitar', lastboardid);

insert into users(name, email) values
    ('Fatima', 'fatima@example.org')
    returning id into lastuserid;
insert into userstokens(token, userId, creationDate) values
    ('6d061c83-707f-4143-9c66-5128a6c5ea63',lastuserid,'2023-02-23 18:31:00');

insert into usersboards(userid, boardid) values (lastuserid,lastboardid);

insert into users(name, email) values
    ('Miguel', 'miguel@example.org')
    returning id into lastuserid;
insert into userstokens(token, userId, creationDate) values
    ('95b36fe5-a100-462c-9123-dc310f92defc',lastuserid,'2023-02-23 18:31:30');

insert into usersboards(userid, boardid) values (lastuserid,lastboardid);

end $$; --anonymous scope

commit; --transaction


abort ;
