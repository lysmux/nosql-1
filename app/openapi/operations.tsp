import "@typespec/http";

using TypeSpec.Http;

namespace NotificationSystem;

// ---------------------------------------------------------------------------
// БП-1. Вход оператора и жизненный цикл сессии
// ---------------------------------------------------------------------------

@route("/auth")
@tag("Сессия оператора")
namespace Auth {
  @doc("""
    Вход оператора. Пароля нет — вход только подтверждает начало работы.
    Создаёт сессию на 30 минут и ставит её токен в httpOnly-cookie `session_token`,
    в теле ответа токена нет. Каждый успешный запрос сдвигает `expiresAt` вперёд.
    """)
  @useAuth(NoAuth)
  @post
  @route("/login")
  op login(): {
    @header("Set-Cookie") setCookie: string;
    @body session: OperatorSession;
  };

  @doc("Выход оператора: сессия удаляется из хранилища, cookie `session_token` сбрасывается.")
  @post
  @route("/logout")
  op logout(): {
    @statusCode statusCode: 204;
    @header("Set-Cookie") setCookie: string;
  } | ErrorResponse;
}

// ---------------------------------------------------------------------------
// БП-2. Главная страница: счётчик посещений
// ---------------------------------------------------------------------------

@route("/main-page")
@tag("Главная страница")
namespace MainPage {
  @doc("""
    Регистрирует посещение главной страницы: атомарно увеличивает счётчик
    `main-page` на единицу и возвращает новое значение.
    Списки заказов и лента уведомлений запрашиваются отдельными операциями.
    """)
  @post
  @route("/visits")
  op registerVisit(): PageCounter | ErrorResponse;
}

// ---------------------------------------------------------------------------
// БП-3, БП-4, БП-6, БП-7. Клиенты
// ---------------------------------------------------------------------------

@route("/users")
@tag("Клиенты")
namespace Users {
  @doc("БП-3. Создаёт клиента. После создания он доступен для выбора при оформлении заказа.")
  @post
  op create(@body body: CreateUserRequest): {
    @statusCode statusCode: 201;
    @body user: User;
  } | ErrorResponse;

  @doc("""
    Полнотекстовый поиск клиентов по имени: совпадение по началу слов, без учёта регистра.
    Без `query` возвращает первых клиентов по алфавиту.
    """)
  @get
  op search(
    @doc("Строка поиска.")
    @query
    query?: string,

    @doc("Сколько записей вернуть.")
    @query
    @minValue(1)
    @maxValue(50)
    limit?: int32 = 10,
  ): User[] | ErrorResponse;

  @doc("БП-6. Профиль клиента. Чтение идёт через кэш: при промахе профиль берётся из основных данных.")
  @get
  @route("/{userId}")
  op read(@path userId: uuid): User | ErrorResponse;

  @doc("""
    БП-7, обязательный сценарий. История уведомлений конкретного клиента,
    отсортированная по `createdAt` по убыванию.
    """)
  @get
  @route("/{userId}/notifications")
  op notifications(
    @path userId: uuid,

    @doc("Сколько записей вернуть.")
    @query
    @minValue(1)
    @maxValue(200)
    limit?: int32 = 50,
  ): Notification[] | ErrorResponse;
}

// ---------------------------------------------------------------------------
// БП-4, БП-5. Заказы
// ---------------------------------------------------------------------------

@route("/orders")
@tag("Заказы")
namespace Orders {
  @doc("БП-2. Страница заказов для главной страницы, от новых к старым.")
  @get
  op list(
    @doc("Номер страницы, с нуля.")
    @query
    @minValue(0)
    page?: int32 = 0,

    @doc("Размер страницы.")
    @query
    @minValue(1)
    @maxValue(100)
    size?: int32 = 20,
  ): OrderPage | ErrorResponse;

  @doc("""
    БП-4. Создаёт заказ на существующего клиента в статусе `CREATED`
    и создаёт клиенту уведомление о принятом заказе.
    """)
  @post
  op create(@body body: CreateOrderRequest): {
    @statusCode statusCode: 201;
    @body order: Order;
  } | ErrorResponse;

  @doc("""
    БП-5. Продвигает заказ на следующий статус по цепочке
    `CREATED → COOKING → IN_DELIVERY → DELIVERED` и создаёт уведомление клиенту.
    Для заказа в статусе `DELIVERED` возвращает 409.
    """)
  @post
  @route("/{orderId}/advance")
  op advance(@path orderId: uuid): Order | ErrorResponse;
}

// ---------------------------------------------------------------------------
// БП-2. Лента уведомлений
// ---------------------------------------------------------------------------

@route("/notifications")
@tag("Уведомления")
namespace Notifications {
  @doc("""
    Лента последних уведомлений по всем клиентам для главной страницы,
    отсортированная по `createdAt` по убыванию.
    """)
  @get
  op feed(
    @doc("Сколько записей вернуть.")
    @query
    @minValue(1)
    @maxValue(200)
    limit?: int32 = 20,
  ): Notification[] | ErrorResponse;
}

// ---------------------------------------------------------------------------
// Настройки
// ---------------------------------------------------------------------------

@route("/settings/cache")
@tag("Настройки")
namespace Settings {
  @doc("Текущее состояние кэша профилей клиентов.")
  @get
  op readCache(): CacheSettings | ErrorResponse;

  @doc("""
    Включает или выключает кэш профилей клиентов без перезапуска.
    Состояние хранится в памяти и после перезапуска возвращается к значению из конфигурации.
    """)
  @put
  op updateCache(@body body: CacheSettings): CacheSettings | ErrorResponse;
}
