# Путешествие дня
Необходимо протестировать веб-сервис, который предлагает купить тур по определённой цене двумя способами:
1. Обычная оплата по дебетовой карте.
2. Уникальная технология: выдача кредита по данным банковской карты.

## Начало работы
Для получения копии этого проекта и его запуска на локальном ПК необходимо клонировать  [репозиторий](https://github.com/Ekaterina7121994/Diploma_project).
Последовательность действий:
1. Открыть [репозиторий](https://github.com/Ekaterina7121994/Diploma_project)
2. Нажать на кнопку ![image](https://github.com/Ekaterina7121994/Diploma_project/assets/139957663/e93bf0fc-c20e-4588-aac0-0a4a0be31487)
3. В открывшемся меню выбрать вкладку SSH и скопировать ссылку на репозиторий
4. Открыть терминал (Git Bash Here) для папки в которой будет хранится репозиторий

**Важно:** Наименование всех папок в пути к папке репозитория включительно, должно быть на латинице, для корректной работы всех программ

5. В терминала вводим команду git clone и скопированную ссылку на репозиторий, нажимаем Enter

**Пример:** git clone git@github.com:Ekaterina7121994/Diploma_project.git

6. Открыть в IntelliJ IDEA Community Edition 2023.2 клонированный проект 

## Prerequisites
Для работы с проектом вам потребуется:

    1. ПК с установленной на нем операционной системой (Windows 11 Домашняя для одного языка 22H2)
    2. Git
    3. Браузер (Google Chrome)
    4. Docker (version 24.0.7)
    5. IntelliJ IDEA Community Edition 2023.2
    6. Java (OpenJDK 11)

## Установка и запуск
    1. Установить всё программное обеспечение из пункта Prerequisites
    2. Клонировать репозиторий на ПК
    3. Запустить программу Docker
    4. Открыть проект в IntelliJ IDEA
    5. Открыть терминал в IntelliJ IDEA (ввести каждую команду в новой вкладке Local)
    6. Запускаем контейнер командой: docker-compose up --build
    7. Запускаем Jar файл:
        Для БД MySQL: java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar   
        Для БД PostgreSQL: java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar
    8. Запускаем автотесты:
        Для БД MySQL: ./gradlew clean test "-Ddb.url=jdbc:mysql://localhost:3306/app"  
        Для БД PostgreSQL: ./gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"  

## Лицензия
Программное обеспечение используемое в данном проекте бесплатно, для домашнего и учебного использования
