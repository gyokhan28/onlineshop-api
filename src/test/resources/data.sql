INSERT INTO job_types (id, name) VALUES
(0, 'ADMIN'),
(1, 'TEST'),
(2, 'CASHIER'),
(3, 'SALES_ASSOCIATE'),
(4, 'SALES_ASSISTANT'),
(5, 'STORE_MANAGER');

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_EMPLOYEE'),
(3, 'ROLE_USER');

INSERT INTO order_status (id, name) VALUES
(0, 'BASKET'),
(1, 'PENDING'),
(2, 'PROCESSING'),
(3, 'SHIPPED'),
(4, 'OUT_FOR_DELIVERY'),
(5, 'DELIVERED'),
(6, 'CANCELLED'),
(7, 'ON_HOLD'),
(8, 'RETURNED'),
(9, 'REFUNDED');

INSERT INTO employees (id, created_at, date_of_birth, email, first_name, is_enabled, last_name, password, phone_number, salary, username, job_type_id, role_id) VALUES
(1, '2024-08-01 21:32:03', '1980-08-01', 'test1@abv.bg', 'Employee', FALSE, '1', '123', '0888888887', '2000', 'test1', 5, 2),
(2, '2024-08-01 21:32:03', '1990-08-01', 'test2@abv.bg', 'Employee', TRUE, '2', '123', '0888888886', '1500', 'test2', 2, 2);

INSERT INTO users (id, created_at, email, first_name, is_enabled, last_name, password, phone_number, username, address_id, role_id) VALUES
(1, NULL, 'user@abv.bg', 'John', TRUE, 'Doe1', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888123456', 'user', NULL, 3),
(2, NULL, 'user2@abv.bg', 'John', TRUE, 'Doe2', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888234567', 'user2', NULL, 3);

INSERT INTO orders (order_cancel_date_time, order_date_time, order_delivery_date_time, status_id, user_id) VALUES
(NULL, '2024-07-17 22:51:47.000000', '2024-07-20 20:32:43.000000', 3, 1),
(NULL, '2024-07-20 22:51:47.000000', NULL, 2, 1);

INSERT INTO product (product, image_urls, is_deleted, name, price, quantity, best_before, expiry_date, is_non_slip, is_outdoor, is_biodegradable, is_reusable, brand_id, color_id, material_id) VALUES
('Drink', 'localhost:8082/api/image/download/1/Screenshot 2024-07-20 212004.png,localhost:8082/api/image/download/1/Screenshot 2024-07-20 212033.png,localhost:8082/api/image/download/1/trattoria-bankia.jpg', FALSE, 'Voda Bankq', 0.90, 200, '2027-09-30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('Drink', 'localhost:8082/api/image/download/2/0010475_boza-magi-1l_550.jpeg,localhost:8082/api/image/download/2/448505897_2471743453214720_6003364360989055017_n.jpg,localhost:8082/api/image/download/2/boza-drink-near-me-Toronto_1024x1024.jpg,localhost:8082/api/image/download/2/boza.jpg,localhost:8082/api/image/download/2/images.jpeg', FALSE, 'Boza', 0.90, 200, '2027-09-30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO order_product (product_price_when_purchased, quantity, order_id, product_id) VALUES
(0.85, 2, 1, 1),
(0.95, 4, 1, 2);