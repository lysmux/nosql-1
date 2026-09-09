# Система уведомлений сервиса доставки еды

Лабораторная работа №1 (NoSQL), вариант №1871523
## Состав репозитория

| Путь | Что внутри |
|---|---|
| `app/` | Бэкенд: Kotlin, Spring Boot 4, Java 21 |
| `app/openapi/` | Контракт API на TypeSpec и сгенерированная из него спецификация OpenAPI 3.1 |
| `web/` | Фронтенд: Svelte 5, Vite |
| `compose.yaml` | Запуск всего проекта |
| `.gitlab-ci.yml` | Параллельная сборка образов бэкенда и фронтенда |

## Хранение данных

У каждого DAO две реализации, выбор — переменной `APP_STORAGE`: `riak` (по умолчанию) или `memory`.

Раскладка в Riak, `тип бакета / бакет / ключ`:

| Данные | Где | Значение |
|---|---|---|
| Клиенты, заказы, уведомления | `default / users, orders, notifications / {id}` | JSON |
| Сессии оператора | `default / sessions / {token}` | `expiresAt` |
| Кэш профилей клиентов | `default / user_cache / {userId}` | Профиль и `cachedAt` |
| Посещения главной страницы | `counters / page_counters / main-page` | CRDT-счётчик |
| Списки и история | `sets / indexes / users, orders, notifications`<br>`sets / user_notifications / {userId}` | CRDT-множества идентификаторов |

В кластере бэкенд Bitcask, вторичных индексов в нём нет, поэтому списки читаются через множества идентификаторов. Время жизни сессий и кэша проверяется при чтении.

Типы бакетов должны существовать до запуска бэкенда — он проверяет их при старте. Создать один раз на любой ноде:

```bash
riak admin bucket-type create counters '{"props":{"datatype":"counter"}}'
riak admin bucket-type activate counters
riak admin bucket-type create sets '{"props":{"datatype":"set"}}'
riak admin bucket-type activate sets
```

С Riak бэкенд работает по Protocol Buffers через [OpenRiak riak-java-client](https://github.com/OpenRiak/riak-java-client). Клиент  лежит в `app/libs/`.

## Запуск

Весь проект — http://localhost:8080:

```bash
docker compose up --build
APP_STORAGE=memory docker compose up --build   # без Riak
```

Разработка, нужны JDK 21 и Node.js 20+:

```bash
cd app && ./gradlew bootRun              # http://localhost:8080
cd web && npm install && npm run dev     # http://localhost:5173
```

После правок контракта на TypeSpec спецификация пересобирается командой `cd app/openapi && npm install && npm run build`.

## Настройки

| Переменная | По умолчанию | Смысл |
|---|---|---|
| `APP_STORAGE` | `riak` | Реализация хранилища: `riak` или `memory` |
| `RIAK_NODES` | `{1,2,3}.riak.dc.lysmux.dev:8087` | Ноды Riak, `host:port` через запятую |
| `BACKEND_URL` | `http://backend:8080` в образе,<br>`http://localhost:8080` в разработке | Куда фронтенд проксирует `/api` |

Время жизни сессии и кэша профилей задаётся в `app/src/main/resources/application.properties`.
