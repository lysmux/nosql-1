<script>
  import { api } from './api.js';
  import Modal from './Modal.svelte';
  import NotificationList from './NotificationList.svelte';

  let { userId, onClose } = $props();

  let user = $state(null);
  let notifications = $state([]);
  let error = $state('');

  $effect(() => {
    Promise.all([api.getUser(userId), api.userNotifications(userId)])
      .then(([loadedUser, loadedNotifications]) => {
        user = loadedUser;
        notifications = loadedNotifications;
      })
      .catch((e) => (error = e.message));
  });
</script>

<Modal title={user ? user.name : 'Профиль клиента'} {onClose}>
  {#if error}
    <p class="error">{error}</p>
  {:else if user}
    <code class="muted">{user.userId}</code>
    <h3>Уведомления клиента</h3>
    <NotificationList {notifications} />
  {:else}
    <p class="muted">Загрузка…</p>
  {/if}
</Modal>

<style>
  code {
    padding: 6px 10px;
    font-size: 12px;
    background: var(--bg);
    border: 1px solid var(--line);
    border-radius: 8px;
  }

  h3 {
    font-size: 12px;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--muted);
  }
</style>
