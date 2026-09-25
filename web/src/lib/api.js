const BASE = '/api';

let onUnauthorized = () => {};

export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler;
}

async function request(method, path, body) {
  const response = await fetch(BASE + path, {
    method,
    credentials: 'include',
    headers: body ? { 'Content-Type': 'application/json' } : undefined,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (response.status === 401) {
    onUnauthorized();
  }
  if (!response.ok) {
    const error = await response.json().catch(() => ({}));
    throw new Error(error.message ?? 'Ошибка запроса');
  }
  return response.status === 204 ? null : response.json();
}

export const api = {
  login: () => request('POST', '/auth/login'),
  logout: () => request('POST', '/auth/logout'),
  registerVisit: () => request('POST', '/main-page/visits'),
  searchUsers: (query) => request('GET', `/users?query=${encodeURIComponent(query)}`),
  createUser: (name) => request('POST', '/users', { name }),
  getUser: (userId) => request('GET', `/users/${userId}`),
  userNotifications: (userId) => request('GET', `/users/${userId}/notifications`),
  listOrders: (page, size) => request('GET', `/orders?page=${page}&size=${size}`),
  createOrder: (order) => request('POST', '/orders', order),
  advanceOrder: (orderId) => request('POST', `/orders/${orderId}/advance`),
  feed: (limit) => request('GET', `/notifications?limit=${limit}`),
  getCacheSettings: () => request('GET', '/settings/cache'),
  setCacheEnabled: (enabled) => request('PUT', '/settings/cache', { enabled }),
};

export const STATUSES = ['CREATED', 'COOKING', 'IN_DELIVERY', 'DELIVERED'];

export const STATUS_LABELS = {
  CREATED: 'Создан',
  COOKING: 'Готовится',
  IN_DELIVERY: 'В доставке',
  DELIVERED: 'Доставлен',
};

export function formatTime(iso) {
  return new Date(iso).toLocaleString('ru-RU', { dateStyle: 'short', timeStyle: 'short' });
}
