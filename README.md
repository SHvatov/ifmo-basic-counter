# Запуск приложения в Docker

1. Создание образа
```
docker build -t s.a.khvatov-basic-counter:latest .
```
2. Развертвание приложения
```
docker run --name s.a.khvatov-basic-counter -d -P s.a.khvatov-basic-counter:latest
```
3. Используя docker-compose
```
docker-compose up
```
