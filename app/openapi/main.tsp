// Контракт API системы уведомлений сервиса доставки еды.
// Предметная область описана в ../../notification-system-domain.md
// Сборка OpenAPI: npm install && npm run build

import "@typespec/http";
import "@typespec/openapi";

import "./models.tsp";
import "./operations.tsp";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

@service(#{ title: "Система уведомлений сервиса доставки еды" })
@info(#{ version: "1.0.0" })
@server("http://localhost:8080", "Локальный запуск")
@doc("""
  Однопользовательский API: единственный клиент — интерфейс оператора.
  Клиенты сервиса (`User`) в систему не входят и своих операций не имеют.
  Уведомления создаются только системой — при создании заказа и при каждой
  смене его статуса. Отдельной операции отправки уведомления в API нет.
  """)
@useAuth(ApiKeyAuth<ApiKeyLocation.cookie, "session_token">)
namespace NotificationSystem;
