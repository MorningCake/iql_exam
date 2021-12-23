create table iql_user.profiles (
    id int8 not null primary key,
    cash int4 not null,
    start_cash int4 not null
);

create table iql_user.credentials (
    id int8 not null primary key,
    login varchar(30) not null unique,
    hash varchar(72) not null,
    role varchar(30) not null
);

create table iql_user.users (
    id int8 not null primary key,
    age int4 not null check (age > 0),
    email text not null unique,
    name text not null,
    credentials_id int8 not null references iql_user.credentials (id),
    profile_id int8 not null references iql_user.profiles (id)
);

create table iql_user.phones (
    id int8 not null primary key,
    value varchar(20) not null unique,
    user_id int8 references iql_user.users (id)
);

create index on iql_user.users (name);