<script>
  import { api, setUnauthorizedHandler } from './lib/api.js';
  import LoginScreen from './lib/LoginScreen.svelte';
  import MainScreen from './lib/MainScreen.svelte';

  let authorized = $state(null);

  setUnauthorizedHandler(() => (authorized = false));

  api.listUsers()
    .then(() => (authorized = true))
    .catch(() => (authorized = false));
</script>

{#if authorized === null}
  <p class="checking">Проверяем сессию…</p>
{:else if authorized}
  <MainScreen onLogout={() => (authorized = false)} />
{:else}
  <LoginScreen onLogin={() => (authorized = true)} />
{/if}

<style>
  .checking {
    margin: 80px auto;
    text-align: center;
    color: var(--muted);
  }
</style>
