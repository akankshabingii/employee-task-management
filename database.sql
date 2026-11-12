CREATE DATABASE employee_task_management;
use employee_task_management;
select database();
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    team_id INT,
    manager_id INT
);
SHOW TABLES;
DESCRIBE users;
CREATE TABLE teams (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);
ALTER TABLE users
ADD CONSTRAINT fk_user_team
FOREIGN KEY (team_id)
REFERENCES teams(id);

ALTER TABLE users
ADD CONSTRAINT fk_user_manager
FOREIGN KEY (manager_id)
REFERENCES users(id);
DESCRIBE users;
SHOW CREATE TABLE users;

DESCRIBE tasks;
INSERT INTO teams (name)
VALUES
('Backend'),
('Frontend'),
('QA');
SELECT * FROM teams;
INSERT INTO users
(name, email, password, role, team_id, manager_id)
VALUES
('Rahul', 'rahul@example.com', 'password123', 'MANAGER', 1, NULL);
INSERT INTO users
(name, email, password, role, team_id, manager_id)
VALUES
('Akanksha', 'akanksha@example.com', 'password123', 'EMPLOYEE', 1, 1),
('Priya', 'priya@example.com', 'password123', 'EMPLOYEE', 1, 1);
SELECT * FROM users;
INSERT INTO tasks
(title, priority, progress, description, start_date, end_date, employee_id, manager_id)
VALUES
(
    'Build Login API',
    'HIGH',
    20,
    'Develop the login API for the application',
    '2026-08-21',
    '2026-08-30',
    2,
    1
),
(
    'Create Dashboard UI',
    'MEDIUM',
    50,
    'Build the employee dashboard',
    '2026-08-21',
    '2026-09-05',
    3,
    1
);
SELECT * FROM tasks;
INSERT INTO users
(name, email, password, role, team_id, manager_id)
VALUES
('Admin', 'admin@example.com', 'password123', 'ADMIN', NULL, NULL);

SELECT
    u.id,
    u.name,
    u.role,
    t.name AS team
FROM users u
JOIN teams t
    ON u.team_id = t.id;
    
SELECT
    e.id,
    e.name AS employee,
    m.name AS manager
FROM users e
JOIN users m
    ON e.manager_id = m.id;
    
SELECT
    t.id,
    t.title,
    u.name AS employee
FROM tasks t
JOIN users u
    ON t.employee_id = u.id;
    
SELECT
    t.id,
    t.title,
    e.name AS employee,
    m.name AS manager
FROM tasks t
JOIN users e
    ON t.employee_id = e.id
JOIN users m
    ON t.manager_id = m.id;
    
SELECT
    u.id,
    u.name,
    u.role,
    t.name AS team
FROM users u
LEFT JOIN teams t
    ON u.team_id = t.id;
    
SELECT *
FROM tasks
WHERE employee_id = 2;


SELECT
    id,
    name,
    email,
    role
FROM users
WHERE manager_id = 1;

SELECT
    t.id,
    t.title,
    t.priority,
    t.progress,
    e.name AS employee
FROM tasks t
JOIN users e
    ON t.employee_id = e.id
WHERE t.manager_id = 1;


SELECT
    u.id,
    u.name,
    u.role,
    t.name AS team
FROM users u
JOIN teams t
    ON u.team_id = t.id
WHERE t.id = 1;

select * from tasks;

USE employee_task_management;

SHOW TABLES;


DESCRIBE users;
DESCRIBE teams;
DESCRIBE tasks;
use employee_task_management;
SELECT id, name, email, password, role FROM users;
select * from tasks;

SELECT id, name, email, password, role
FROM users;

UPDATE users
SET password = '$2a$10$toCqAxfjizU5v8e6s.4mPukM3quz239KGIO1X66aPayWaCLg6G2R2'
WHERE id = 1;
SELECT id, name, email, role
FROM users;
UPDATE users
SET password = '$2a$10$toCqAxfjizU5v8e6s.4mPukM3quz239KGIO1X66aPayWaCLg6G2R2'
WHERE id = 4;


SELECT id, email, role, password
FROM users;

UPDATE users
SET password = '$2a$10$toCqAxfjizU5v8e6s.4mPukM3quz239KGIO1X66aPayWaCLg6G2R2'
WHERE id = 3;

SELECT id, email, role, password
FROM users
WHERE id = 3;

SELECT id, name, email, role, manager_id, team_id
FROM users;

select * from tasks;

SELECT id, name, role, team_id, manager_id
FROM users
WHERE id = 15;

UPDATE tasks
SET status = "PENDING"
WHERE id = 5,6,7;


UPDATE tasks
SET status = 'PENDING'
WHERE id IN (5, 6, 7);


SELECT id, name, role, team_id, manager_id
FROM users
WHERE id = 14;

SELECT id, name, role, team_id
FROM users
WHERE role = 'MANAGER';

USE employee_task_management;
SELECT id, name, email, role FROM users;

SELECT id, name, email, password, role
FROM users;

SELECT id, email, password, role
FROM users
WHERE email = 'rahul4@example.com';

UPDATE users
SET password = '$2a$10$O6AG.gV28SLAr.tDf5uzwes5/R5rqZI9MB4gLQBvHoHluv87NbI5q'
WHERE email = 'rahul4@example.com';

SELECT id, email, password, role
FROM users;


USE employee_task_management;

SELECT id, name, email, role, team_id
FROM users;
USE employee_task_management;

SELECT id, email, password
FROM users
WHERE email = 'testemployee@example.com';

SELECT id, email, password, role
FROM users;

SELECT id, email, password, role
FROM users
WHERE role = 'ADMIN';

select * from users;

delete from users;

SET SQL_SAFE_UPDATES = 0;

DELETE FROM users;

SET SQL_SAFE_UPDATES = 1;

SELECT id, name, email, role, team_id, manager_id
FROM users
WHERE email IN (
    'newadmin@example.com',
    'newmanager@example.com',
    'newemployee@example.com'
);

DESCRIBE users;

SELECT id, email, role, password
FROM users
WHERE email = 'newadmin@example.com';

