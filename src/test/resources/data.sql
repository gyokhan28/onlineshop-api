INSERT INTO `job_types` (`id`, `name`) VALUES
(0, 'ADMIN'),
(1, 'TEST'),
(2, 'CASHIER'),
(3, 'SALES_ASSOCIATE'),
(4, 'SALES_ASSISTANT'),
(5, 'STORE_MANAGER');
INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_EMPLOYEE'),
(3, 'ROLE_USER');
INSERT INTO `employees` (`id`, `created_at`, `date_of_birth`, `email`, `first_name`, `is_enabled`, `last_name`, `password`, `phone_number`, `salary`, `username`, `job_type_id`, `role_id`) VALUES (1, '2024-08-01 21:32:03.000000', '1980-08-01', 'test1@abv.bg', 'Employee', b'0', '1', '123', '0888888887', '2000', 'test1', '5', '2'), (2, '2024-08-01 21:32:03.000000', '1990-08-01', 'test2@abv.bg', 'Employee', b'1', '2', '123', '0888888886', '1500', 'test2', '2', '2');