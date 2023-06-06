drop table if exists UsersTokens;
drop table if exists UsersBoards;
drop table if exists Cards;
drop table if exists Lists;
drop table if exists Boards;
drop table if exists Users;

create table Users (
    id serial primary key,
    name varchar(255) not null,
    passwordHash char(64) not null,
    salt char(12) not null,
    email varchar(254) not null unique
);

create table Boards (
    id serial primary key,
    name varchar(255) not null unique,
    description varchar(255) not null
);

create table Lists (
    id serial primary key,
    name varchar(255) not null,
    boardId int references Boards(id) not null,
    nCards int default 0
);

create table Cards (
    id serial primary key,
    name varchar(255) not null,
    description varchar(255) not null,
    dueDate timestamp, -- Sem input check por agora para ver se a data é no futuro
    listId int references Lists(id) on delete cascade,
    boardId int references Boards(id) not null,
    cidx int not null
);

create table UsersBoards (
    userId int references Users(id) not null,
    boardId int references Boards(id) not null,
    primary key(userId, boardId)
);

create table UsersTokens (
    token varchar(128) not null,
    userId int references Users(id) on delete cascade not null ,
    creationDate timestamp not null,
    primary key(token, userId, creationDate)
);