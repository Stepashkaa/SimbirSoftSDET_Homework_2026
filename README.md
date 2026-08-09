## Allure Report

### Overview
![Allure Overview](docs/images/allure-overview.png)

### Suites
![Allure Suites](docs/images/allure-suites.png)

### Graphs
![Allure Suites](docs/images/allure-graphs.png)

## CI/CD

В проекте настроен автоматический запуск UI-тестов через GitHub Actions.

Workflow запускается:
- при push в ветки `main` и `dev/ui-test`
- при создании Pull Request в `main`

Pipeline выполняет:
- checkout проекта
- настройку Java 17
- запуск Maven тестов
- сохранение test reports и Allure results как artifacts