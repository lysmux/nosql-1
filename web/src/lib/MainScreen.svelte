<script>
  import { api } from './api.js';
  import OrderCard from './OrderCard.svelte';
  import NotificationList from './NotificationList.svelte';
  import UserProfile from './UserProfile.svelte';
  import CreateUserDialog from './CreateUserDialog.svelte';
  import CreateOrderDialog from './CreateOrderDialog.svelte';
  import Pagination from './Pagination.svelte';

  const PAGE_SIZE = 3;

  let { onLogout } = $props();

  let ordersPage = $state({ items: [], page: 0, totalItems: 0, totalPages: 0 });
  let page = $state(0);
  let notifications = $state([]);
  let visits = $state(null);
  let cacheEnabled = $state(null);
  let error = $state('');
  let dialog = $state(null);
  let profileUserId = $state(null);

  async function reload() {
    try {
      [ordersPage, notifications] = await Promise.all([
        api.listOrders(page, PAGE_SIZE),
        api.feed(50),
      ]);
      error = '';
    } catch (e) {
      error = e.message;
    }
  }

  function goToPage(next) {
    page = next;
    return reload();
  }

  async function toggleCache() {
    try {
      cacheEnabled = (await api.setCacheEnabled(!cacheEnabled)).enabled;
    } catch (e) {
      error = e.message;
    }
  }

  async function advance(orderId) {
    try {
      await api.advanceOrder(orderId);
      await reload();
    } catch (e) {
      error = e.message;
    }
  }

  async function logout() {
    try {
      await api.logout();
    } finally {
      onLogout();
    }
  }

  api.registerVisit()
    .then((counter) => (visits = counter.value))
    .catch(() => {});
  api.getCacheSettings()
    .then((settings) => (cacheEnabled = settings.enabled))
    .catch(() => {});
  reload();
</script>

<header class="top">
  <h1><span class="logo">🍔</span> Заказы и уведомления</h1>
  <div class="actions">
    {#if visits !== null}<span class="visits">Посещений: <b>{visits}</b></span>{/if}
    {#if cacheEnabled !== null}
      <button class:primary={cacheEnabled} onclick={toggleCache}>Кэш: {cacheEnabled ? 'вкл' : 'выкл'}</button>
    {/if}
    <button onclick={() => (dialog = 'user')}>+ Клиент</button>
    <button onclick={() => (dialog = 'order')}>+ Заказ</button>
    <button onclick={reload}>Обновить</button>
    <button onclick={logout}>Выйти</button>
  </div>
</header>

{#if error}<p class="error banner">{error}</p>{/if}

<main>
  <section>
    <h2>Заказы <span class="count">{ordersPage.totalItems}</span></h2>
    <div class="orders">
      {#if ordersPage.items.length === 0}
        <p class="empty muted">Заказов пока нет. Создайте первый.</p>
      {:else}
        {#each ordersPage.items as order (order.orderId)}
          <OrderCard
            {order}
            onOpenUser={(userId) => (profileUserId = userId)}
            onAdvance={advance}
          />
        {/each}
      {/if}
    </div>
    <Pagination {page} totalPages={ordersPage.totalPages} onChange={goToPage} />
  </section>

  <aside>
    <h2>Лента уведомлений</h2>
    <div class="feed">
      <NotificationList {notifications} showUser />
    </div>
  </aside>
</main>

{#if dialog === 'user'}
  <CreateUserDialog
    onCreated={() => {
      dialog = null;
      reload();
    }}
    onClose={() => (dialog = null)}
  />
{:else if dialog === 'order'}
  <CreateOrderDialog
    onCreated={() => {
      dialog = null;
      goToPage(0);
    }}
    onClose={() => (dialog = null)}
  />
{/if}

{#if profileUserId}
  <UserProfile userId={profileUserId} onClose={() => (profileUserId = null)} />
{/if}

<style>
  .top {
    position: sticky;
    top: 0;
    z-index: 5;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 24px;
    background: rgba(14, 16, 20, 0.75);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid var(--line);
  }

  h1 {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 17px;
  }

  .logo {
    display: grid;
    place-items: center;
    width: 30px;
    height: 30px;
    font-size: 15px;
    background: var(--accent-dim);
    border: 1px solid rgba(255, 138, 61, 0.3);
    border-radius: 10px;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .visits {
    margin-right: 4px;
    font-size: 13px;
    color: var(--muted);
  }

  .visits b { color: var(--text); }

  .banner {
    margin: 0;
    padding: 12px 24px;
    background: rgba(255, 107, 107, 0.1);
    border-bottom: 1px solid rgba(255, 107, 107, 0.25);
  }

  main {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 380px;
    gap: 22px;
    align-items: start;
    max-width: 1280px;
    margin: 0 auto;
    padding: 24px;
  }

  h2 {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 14px;
    font-size: 12px;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--muted);
  }

  .count {
    padding: 1px 8px;
    font-size: 11px;
    color: var(--accent);
    background: var(--accent-dim);
    border-radius: 999px;
  }

  .orders {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  aside {
    position: sticky;
    top: 82px;
    padding: 18px;
    background: linear-gradient(180deg, var(--panel), #14171f);
    border: 1px solid var(--line);
    border-radius: var(--radius);
  }

  .feed {
    max-height: calc(100vh - 200px);
    overflow-y: auto;
    padding-right: 4px;
  }

  .empty {
    padding: 40px 0;
    text-align: center;
  }

  @media (max-width: 900px) {
    main { grid-template-columns: 1fr; }
    aside { position: static; }
  }
</style>
