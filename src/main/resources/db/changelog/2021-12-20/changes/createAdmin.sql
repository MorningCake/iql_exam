insert into iql_user.profiles (id, cash, start_cash) values (1, 0, 0);
insert into iql_user.credentials(id, login, hash, role) values
    (2, 'a', '$2a$12$BKvKl7sTZgZg6SQeGMyJGODwF5O8ELKAz4YbWJr40aRtpXVvyOi8S', 'ADMIN');
insert into iql_user.users (id, age, email, name, credentials_id, profile_id) values (3, 30, 'admin@mail.ad', 'Ad', 2, 1);
insert into iql_user.phones (id, value, user_id) values (4, '+79271112233', 3);