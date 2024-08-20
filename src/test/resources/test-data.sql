-- Test data for the test database

-- Insert test data into `material`
INSERT INTO `material` (`id`, `name`) VALUES
(101, 'Test Wood'),
(102, 'Test Plastic'),
(103, 'Test Metal'),
(104, 'Test Bamboo'),
(105, 'Test Cotton'),
(106, 'Test Foam'),
(107, 'Test Microfiber'),
(108, 'Test Silk'),
(109, 'Test Paper');

-- Insert test data into `brands`
INSERT INTO `brands` (`id`, `name`) VALUES
(101, 'Test Nike'),
(102, 'Test Adidas'),
(103, 'Test Elektroresurs'),
(104, 'Test The CocaCola Company'),
(105, 'Test RailingSystems LTD'),
(106, 'Test TheDecorationBrand'),
(107, 'Test Mlin Rz');

-- Insert test data into `product_colors`
INSERT INTO `product_colors` (`id`, `name`) VALUES
(101, 'Test Black'),
(102, 'Test White'),
(103, 'Test Green'),
(104, 'Test Red'),
(105, 'Test Blue'),
(106, 'Test Pink'),
(107, 'Test Yellow'),
(108, 'Test Brown'),
(109, 'Test Other');

-- Insert test data into `city`
INSERT INTO `city` (`id`, `name`) VALUES
(101, 'Test Sofia'),
(102, 'Test Plovdiv'),
(103, 'Test Varna'),
(104, 'Test Burgas'),
(105, 'Test Ruse'),
(106, 'Test Stara Zagora'),
(107, 'Test Pleven'),
(108, 'Test Sliven'),
(109, 'Test Dobrich'),
(110, 'Test Shumen'),
(111, 'Test Pernik'),
(112, 'Test Haskovo'),
(113, 'Test Yambol'),
(114, 'Test Pazardzhik'),
(115, 'Test Blagoevgrad'),
(116, 'Test Veliko Tarnovo'),
(117, 'Test Vratsa'),
(118, 'Test Gabrovo'),
(119, 'Test Vidin'),
(120, 'Test Vraca'),
(121, 'Test Kyustendil'),
(122, 'Test Targovishte'),
(123, 'Test Montana'),
(124, 'Test Silistra'),
(125, 'Test Dimitrovgrad'),
(126, 'Test Lovech'),
(127, 'Test Kardzhali'),
(128, 'Test Petrich'),
(129, 'Test Samokov'),
(130, 'Test Kazanlak'),
(131, 'Test Troyan'),
(132, 'Test Svishtov'),
(133, 'Test Razgrad'),
(134, 'Test Sandanski'),
(135, 'Test Balchik'),
(136, 'Test Velingrad'),
(137, 'Test Harmanli'),
(138, 'Test Dupnitsa'),
(139, 'Test Parvomay'),
(140, 'Test Karlovo'),
(141, 'Test Slovengrad'),
(142, 'Test Aytos'),
(143, 'Test Kavarna'),
(144, 'Test Nova Zagora'),
(145, 'Test Gotse Delchev'),
(146, 'Test Simitli'),
(147, 'Test Lom'),
(148, 'Test Sevlievo'),
(149, 'Test Polski Trambesh'),
(150, 'Test Teteven'),
(151, 'Test Svilengrad'),
(152, 'Test Botevgrad'),
(153, 'Test Radomir'),
(154, 'Test Razlog'),
(155, 'Test Elin Pelin'),
(156, 'Test Peshtera'),
(157, 'Test Chirpan'),
(158, 'Test Kubrat'),
(159, 'Test Aleksandrograd'),
(160, 'Test Panagyurishte'),
(161, 'Test Kostenets'),
(162, 'Test Momchilovgrad'),
(163, 'Test Asenugleshen'),
(164, 'Test Sveti Vladimir'),
(165, 'Test Nesebar'),
(166, 'Test Dryanovo'),
(167, 'Test Kostandovo'),
(168, 'Test Popovo'),
(169, 'Test Strazhitsa'),
(170, 'Test Tsarevo'),
(171, 'Test Tutrakan'),
(172, 'Test Etropole'),
(173, 'Test Lukovit'),
(174, 'Test Radnevo'),
(175, 'Test Zavet'),
(176, 'Test Strumyani'),
(177, 'Test Sofiyagrad'),
(178, 'Test Knezha'),
(179, 'Test Shivachevo'),
(180, 'Test Dolnichiflik'),
(181, 'Test Pomorie'),
(182, 'Test Bregovo'),
(183, 'Test Shabla'),
(184, 'Test Malko Tarnowo'),
(185, 'Test Iskar'),
(186, 'Test Beli Polets'),
(187, 'Test Svirachi'),
(188, 'Test Dobrichkovo'),
(189, 'Test Tsar Petrovo'),
(190, 'Test Kuklen'),
(191, 'Test Krichim'),
(192, 'Test Lyaskovets'),
(193, 'Test Medkovec'),
(194, 'Test Medovsk'),
(195, 'Test Breznitsa'),
(196, 'Test Medni Log'),
(197, 'Test Staro Oryahovo');

-- Insert test data into `order_status`
INSERT INTO `order_status` (`id`, `name`) VALUES
(10, 'Test BASKET'),
(11, 'Test PENDING'),
(12, 'Test PROCESSING'),
(13, 'Test SHIPPED'),
(14, 'Test OUT_FOR_DELIVERY'),
(15, 'Test DELIVERED'),
(16, 'Test CANCELLED'),
(17, 'Test ON_HOLD'),
(18, 'Test RETURNED'),
(19, 'Test REFUNDED');

-- Insert test data into `roles`
INSERT INTO `roles` (`id`, `name`) VALUES
(10, 'Test ROLE_ADMIN'),
(11, 'Test ROLE_EMPLOYEE'),
(12, 'Test ROLE_USER');

-- Insert test data into `job_types`
INSERT INTO `job_types` (`id`, `name`) VALUES
(10, 'Test ADMIN'),
(11, 'Test TEST'),
(12, 'Test CASHIER'),
(13, 'Test SALES_ASSOCIATE'),
(14, 'Test SALES_ASSISTANT'),
(15, 'Test STORE_MANAGER');

INSERT INTO `address` (`id`, `additional_information`, `street_name`, `city_id`) VALUES
(101, 'This is where the admin lives', 'Admin str.', '33'),
(102, 'Second floor - green door ', 'Ivan Ivanov 22', '38'),
(103, 'Ground floor - behind the building', '6 Септември', '33');

-- Insert test data into `users`
INSERT INTO `users` (`id`, `created_at`, `email`, `first_name`, `is_enabled`, `last_name`, `password`, `phone_number`, `username`, `address_id`, `role_id`) VALUES
(101, NULL, 'testuser@abv.bg', 'TestUser', true, 'Test', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888000001', 'testuser', 101, 12),
(102, NULL, 'testuser2@abv.bg', 'TestUser2', true, 'Test2', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888000002', 'testuser2', 102, 12);

-- Insert test data into `employees`
INSERT INTO `employees` (`id`, `created_at`, `email`, `first_name`, `is_enabled`, `last_name`, `password`, `phone_number`, `username`, `role_id`, `date_of_birth`, `salary`, `job_type_id`) VALUES
(101, '2024-01-01 10:00:00', 'testadmin@abv.bg', 'TestAdmin', TRUE, 'Test', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888000003', 'testadmin', 10, '1980-01-01', 90000, 10),
(102, '2024-08-01 21:32:03', 'testemployee1@abv.bg', 'TestEmployee1', FALSE, 'Test', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888000004', 'testemployee1', 11, '1985-08-01', 2500, 11),
(103, '2024-08-01 21:32:03', 'testemployee2@abv.bg', 'TestEmployee2', TRUE, 'Test', '$2a$12$OUSUaCyYf4wmBX2T5r5QHu57VMVyeqMB5ceHsnHrf9FLRCmXp8dTy', '0888000005', 'testemployee2', 11, '1985-08-01', 2500, 11);


INSERT INTO `product` (`product`, `id`, `is_deleted`, `name`, `price`, `quantity`, `best_before`, `expiry_date`, `is_non_slip`, `is_outdoor`, `is_biodegradable`, `is_reusable`, `brand_id`, `color_id`, `material_id`) VALUES
('Drink', 101, b'0', 'Voda Bankq', '0.90', '200', '2027-09-30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('Drink', 102, b'0', 'Бозичка', '0.90', '200', '2027-09-30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO `orders` (`id`, `order_cancel_date_time`, `order_date_time`, `order_delivery_date_time`, `status_id`, `user_id`) VALUES
(101, NULL, '2024-07-17 22:51:47.000000', '2024-07-20 20:32:43.000000', '3', '1'),
(102, NULL, '2024-07-20 22:51:47.000000', NULL, '2', '1');
INSERT INTO `order_product` (`id`, `product_price_when_purchased`, `quantity`, `order_id`, `product_id`) VALUES
(101, '0.85', '2', '1', '1'),
(102, '0.95', '4', '1', '2');