<script>
  import { api } from './api.js';
  import Modal from './Modal.svelte';
  import UserSearch from './UserSearch.svelte';

  let { onCreated, onClose } = $props();

  let user = $state(null);
  let restaurantName = $state('');
  let totalAmount = $state('');
  let busy = $state(false);
  let error = $state('');

  async function submit(event) {
    event.preventDefault();
    busy = true;
    error = '';
    try {
      await api.createOrder({ userId: user.userId, restaurantName, totalAmount: Number(totalAmount) });
      onCreated();
    } catch (e) {
      error = e.message;
    } finally {
      busy = false;
    }
  }
</script>

<Modal title="Новый заказ" {onClose}>
  <form class="form" onsubmit={submit}>
    <div class="field">
      Клиент
      {#if user}
        <div class="selected">
          <b>{user.name}</b>
          <button type="button" onclick={() => (user = null)}>Сменить</button>
        </div>
      {:else}
        <UserSearch onSelect={(found) => (user = found)} />
      {/if}
    </div>
    <label class="field">
      Ресторан
      <input bind:value={restaurantName} maxlength="200" placeholder="Например, Пицца Хат" required />
    </label>
    <label class="field">
      Сумма, ₽
      <input bind:value={totalAmount} type="number" min="0" step="0.01" placeholder="0.00" required />
    </label>
    {#if error}<p class="error">{error}</p>{/if}
    <button class="primary" type="submit" disabled={busy || !user}>Создать заказ</button>
  </form>
</Modal>

<style>
  .selected {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 6px 6px 6px 12px;
    color: var(--text);
    text-transform: none;
    letter-spacing: normal;
    background: var(--bg);
    border: 1px solid var(--line);
    border-radius: 10px;
  }
</style>
