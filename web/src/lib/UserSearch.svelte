<script>
  import { api } from './api.js';

  let { onSelect } = $props();

  const DEBOUNCE_MS = 250;

  let query = $state('');
  let users = $state([]);
  let loading = $state(false);
  let error = $state('');
  let timer;
  let lastRequest = 0;

  async function search() {
    const text = query.trim();
    if (!text) return;

    const request = ++lastRequest;
    loading = true;
    try {
      const found = await api.searchUsers(text);
      if (request !== lastRequest) return;
      users = found;
      error = '';
    } catch (e) {
      if (request === lastRequest) error = e.message;
    } finally {
      if (request === lastRequest) loading = false;
    }
  }

  function onInput() {
    clearTimeout(timer);
    timer = setTimeout(search, DEBOUNCE_MS);
  }
</script>

<input bind:value={query} oninput={onInput} placeholder="Начните вводить имя клиента" autocomplete="off" />

{#if query.trim()}
  {#if error}
    <p class="error">{error}</p>
  {:else if loading}
    <p class="muted">Поиск…</p>
  {:else if users.length === 0}
    <p class="muted">Ничего не найдено</p>
  {:else}
    <ul>
      {#each users as user (user.userId)}
        <li><button type="button" onclick={() => onSelect(user)}>{user.name}</button></li>
      {/each}
    </ul>
  {/if}
{/if}

<style>
  ul {
    max-height: 180px;
    margin: 0;
    padding: 0;
    overflow-y: auto;
    list-style: none;
    border: 1px solid var(--line);
    border-radius: 10px;
  }

  li button {
    width: 100%;
    text-align: left;
    text-transform: none;
    letter-spacing: normal;
    background: transparent;
    border: none;
    border-radius: 0;
  }

  p { margin: 0; }
</style>
