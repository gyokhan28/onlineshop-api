# Online Shop
Проект - Ниво 4

# Електронен магазин
Искаме да изградим уеб приложение за работа в магазин.

В системата ще има два вида потребители - служители и клиенти. При стартиране на програмата на потребителя се дава възможност да избере като какъв иска да я използва. Всеки служител има уникално employee_id. Всяка стока има уникално product_id.

1. Данни за стоките: product_id,name,price,quantity,type,color,expires_in

Видовете продукти са следните:
- Храни (food)
- Напитки (drinks)
- Санитарни (sanitary)
- Парапети (railing)
- Аксесоари, декорация, други (others)

Важно е да се отбележи, че различните категории продукти имат различни характеристики и структура. 

2. Данни за служителите: employee_id,first_name,last_name,age,salary

## Вход като служител:
- Системата подканва служителя да въведе своето id и парола 
- При успешен вход (служител с такива данни съществува в системата) пред служителя се поставя възможността за избор от меню с опции
- Системата да дава възможност за следните операции: 
  - Управление на продукти: добавяне, промяна и изтриване на продукти

  - Показване на списък с всички продукти и сортиране по:
    - име
    - цена 
    - срок на годност (за продуктите, които имат такъв, най-скоро изтичащите спрямо днешната дата се принтират първи)

- Търсене на продукти: по id, по име, по цена (интервал), по количество наличност
- Добавяне на продукт
- Промяна на продукт (по id)
- Изтриване на продукт (по id, не е нужно id-тата на другите продукти да се променят при това действие)
- Сортиране на служителите по:
  - име
  - заплата 
- Промяна на статуса на поръчка. Възможните статуси са: нова, обработва се, изпратена по куриер, завършена
- Справка с всички поръчки сортиране в хронологичен ред, като най-горе е последната направена поръчка. Възможност за филтриране на поръчките по статус.

## Вход като клиент:
- Отпечатване на всички налични продукти (с количество поне 1)
- Търсене на продукт по категория
- Търсене на продукт по име (както пълно съвпадение, така и частично)
- Добавяне към потребителската кошница по правилно подадено product_id и количество (ако желаното количеството продукти не е налично, да се показва съобщение за грешка)
- Създаване на покупка, която се записва в БД. Покупката съдържа списък с всички продукти, тяхната бройка, цени, дата на поръчка и крайна цена. Като се прави проверка има ли достатъчно наличност. И след завършване на покупката се актуализират наличностите на закупените продукти.

## Допълнителни изисквания:
- Да има 2 проекта - фронтенд+бекенд. Като фронтенда да бъде на React/Spring Boot и да консумира бекенд проекта
- Трябва да се използват Flyway миграции, Lombok, Gradle
- Да има конфигурирани Sonar Analysis & Github Actions
- Да има Custom exceptions & Logging
- Проектът трябва да бъде контейнеризиран

## Бонус изисквания:
- Пазарска кошница
- Генериране на фактури
