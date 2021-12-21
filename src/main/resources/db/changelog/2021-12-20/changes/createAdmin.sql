insert into iql_user.profiles (id, cash, start_cash) values (1, 0, 0);
insert into iql_user.users (id, age, deleted, email, name, profile_id) values (2, 30, false, 'admin@mail.ad', 'Ad', 1);
insert into iql_user.phones (id, value, user_id) values (3, '+79271112233', 2);
insert into iql_user.credentials(id, login, hash, role, user_id) values
    (4, 'a', '$2a$12$BKvKl7sTZgZg6SQeGMyJGODwF5O8ELKAz4YbWJr40aRtpXVvyOi8S', 'ADMIN', 2);