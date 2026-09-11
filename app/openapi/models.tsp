import "@typespec/http";

using TypeSpec.Http;

namespace NotificationSystem;

@format("uuid")
scalar uuid extends string;

// ---------------------------------------------------------------------------
// Предметные сущности
// ---------------------------------------------------------------------------

@doc("Клиент сервиса доставки. Учётных данных нет, заводится оператором.")
model User {
  @doc("Идентификатор клиента.")
  userId: uuid;

  @doc("Имя клиента.")
  name: string;
}

@doc("Статусы заказа. Переход всегда на один шаг вперёд, `DELIVERED` — конечный.")
enum OrderStatus {
  CREATED,
  COOKING,
  IN_DELIVERY,
  DELIVERED,
}

@doc("Заказ клиента — источник всех уведомлений.")
model Order {
  @doc("Идентификатор заказа.")
  orderId: uuid;

  @doc("Заказчик.")
  userId: uuid;

  @doc("Название ресторана. Справочника ресторанов нет, это просто строка.")
  restaurantName: string;

  @doc("Сумма заказа.")
  totalAmount: decimal;

  @doc("Текущий статус.")
  status: OrderStatus;

  @doc("Момент создания заказа.")
  createdAt: utcDateTime;
}

@doc("Уведомление клиенту. Создаётся системой при создании заказа и при смене его статуса.")
model Notification {
  @doc("Идентификатор уведомления.")
  notificationId: uuid;

  @doc("Получатель.")
  userId: uuid;

  @doc("Заказ, породивший уведомление.")
  orderId: uuid;

  @doc("Готовый текст, собранный по текущему статусу заказа.")
  text: string;

  @doc("Момент создания.")
  createdAt: utcDateTime;
}

@doc("Сессия оператора. Живёт 30 минут, продлевается при каждом успешном запросе.")
model OperatorSession {
  @doc("Момент истечения сессии.")
  expiresAt: utcDateTime;
}

@doc("Счётчик посещений главной страницы. Счётчик один, только инкремент.")
model PageCounter {
  @doc("Фиксированное значение `main-page`.")
  pageKey: string;

  @doc("Число посещений после инкремента.")
  value: int64;
}

// ---------------------------------------------------------------------------
// Тела запросов
// ---------------------------------------------------------------------------

@doc("Данные для создания клиента.")
model CreateUserRequest {
  @doc("Имя клиента.")
  @minLength(1)
  @maxLength(120)
  name: string;
}

@doc("Данные для создания заказа. Клиент должен существовать.")
model CreateOrderRequest {
  @doc("Идентификатор существующего клиента.")
  userId: uuid;

  @doc("Название ресторана.")
  @minLength(1)
  @maxLength(200)
  restaurantName: string;

  @doc("Сумма заказа.")
  @minValue(0)
  totalAmount: decimal;
}

// ---------------------------------------------------------------------------
// Ошибки
// ---------------------------------------------------------------------------

@doc("Машиночитаемые коды ошибок.")
enum ErrorCode {
  @doc("Сессия отсутствует, не найдена или истекла.")
  sessionExpired: "session_expired",

  @doc("Объект по указанному идентификатору не найден.")
  notFound: "not_found",

  @doc("Тело запроса не прошло проверку.")
  validationFailed: "validation_failed",

  @doc("Заказ уже в конечном статусе `DELIVERED`, продвигать некуда.")
  orderCompleted: "order_completed",
}

@error
@doc("Ошибка выполнения запроса.")
model ErrorResponse {
  @statusCode statusCode: 400 | 401 | 404 | 409;

  @doc("Код ошибки.")
  code: ErrorCode;

  @doc("Пояснение для оператора.")
  message: string;
}
