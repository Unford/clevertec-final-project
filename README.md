
## Для запуска необходимо:
- В сервисе [rabbit-forward-service](rabbit-forward-service), в файле [.env](rabbit-forward-service/.env), необходимо указать актуальные данные по rabbitmq, с которого будет идти первоначальное наполнение.

  ![image](https://github.com/MaksLaptsev/clevertec-final-project/assets/55844987/b7438f87-8a93-4612-82a8-6f0813077500)
- Далее в gradle, в корневной папке Tasks сборщика присутствует пакет custom_build. Там есть задача `publishStartersAndBootJarInServices`. Эту задачу необходимо запустить для сбора boot Jars всех модулей, а также опубликовать стартеры локально.
- Запустить [docker-compose.yml](docker-compose.yml) в корне проекта c помощью `docker-compose up -d`
 
## Open API документация:
### Документация доступна по следующим адресам:
- http://localhost:8640/swagger-ui/index.html — Credit Service api-docs
- http://localhost:8540/swagger-ui/index.html — Account-Card Service  api-docs
- http://localhost:8083/swagger-ui/index.html — Currency Service api-docs
- http://localhost:8085/swagger-ui/index.html — Deposit Service api-docs
- http://localhost:8082/swagger-ui/index.html — Customer Service api-docs
- http://localhost:9191/swagger-ui/index.html — Auth Service api-docs
