CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(40) NOT NULL,
    last_name VARCHAR(40) NOT NULL,
    username VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL,
    password VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone_number VARCHAR(30),
    is_enabled BOOLEAN,
    created_at TIMESTAMP on update CURRENT_TIMESTAMP NOT NULL
);
--TODO relations

