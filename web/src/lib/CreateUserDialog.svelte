<script>
  import { api } from './api.js';
  import Modal from './Modal.svelte';

  let { onCreated, onClose } = $props();

  let name = $state('');
  let busy = $state(false);
  let error = $state('');

  async function submit(event) {
    event.preventDefault();
    busy = true;
    error = '';
    try {
      await api.createUser(name);
      onCreated();
    } catch (e) {
      error = e.message;
    } finally {
      busy = false;
    }
  }
</script>

<Modal title="Новый клиент" {onClose}>
  <form class="form" onsubmit={submit}>
    <label class="field">
      Имя
      <input bind:value={name} maxlength="120" placeholder="Например, Мария Соколова" required />
    </label>
    {#if error}<p class="error">{error}</p>{/if}
    <button class="primary" type="submit" disabled={busy}>Создать клиента</button>
  </form>
</Modal>
