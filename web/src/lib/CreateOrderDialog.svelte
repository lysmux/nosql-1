<script>
  import { untrack } from 'svelte';
  import { api } from './api.js';
  import Modal from './Modal.svelte';

  let { users, onCreated, onClose } = $props();

  let userId = $state(untrack(() => users[0]?.userId) ?? '');
  let restaurantName = $state('');
  let totalAmount = $state('');
  let busy = $state(false);
  let error = $state('');

  async function submit(event) {
    event.preventDefault();
    busy = true;
    error = '';
    try {
      await api.createOrder({ userId, restaurantName, totalAmount: Number(totalAmount) });
      onCreated();
    } catch (e) {
      error = e.message;
    } finally {
      busy = false;
    }
  }
</script>

<Modal title="Новый заказ" {onClose}>
  {#if users.length === 0}
    <p class="muted">Сначала создайте клиента.</p>
  {:else}
    <form class="form" onsubmit={submit}>
      <label class="field">
        Клиент
        <select bind:value={userId}>
          {#each users as user (user.userId)}
            <option value={user.userId}>{user.name}</option>
          {/each}
        </select>
      </label>
      <label class="field">
        Ресторан
        <input bind:value={restaurantName} maxlength="200" placeholder="Например, Пицца Хат" required />
      </label>
      <label class="field">
        Сумма, ₽
        <input bind:value={totalAmount} type="number" min="0" step="0.01" placeholder="0.00" required />
      </label>
      {#if error}<p class="error">{error}</p>{/if}
      <button class="primary" type="submit" disabled={busy}>Создать заказ</button>
    </form>
  {/if}
</Modal>
