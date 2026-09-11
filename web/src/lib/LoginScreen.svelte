<script>
  import { api } from './api.js';

  let { onLogin } = $props();

  let busy = $state(false);
  let error = $state('');

  async function login() {
    busy = true;
    error = '';
    try {
      await api.login();
      onLogin();
    } catch (e) {
      error = e.message;
    } finally {
      busy = false;
    }
  }
</script>

<div class="screen">
  <div class="card">
    <span class="logo">🍔</span>
    <h1>Система уведомлений</h1>
    <p class="muted">
      Сервис доставки еды. Вход оператора без пароля — сессия живёт 30 минут
      и продлевается при каждом действии.
    </p>
    <button class="primary" disabled={busy} onclick={login}>
      {busy ? 'Входим…' : 'Войти'}
    </button>
    {#if error}<p class="error">{error}</p>{/if}
  </div>
</div>

<style>
  .screen {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
    padding: 20px;
  }

  .card {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
    width: 360px;
    padding: 36px 32px;
    text-align: center;
    background: linear-gradient(180deg, var(--panel), #13161d);
    border: 1px solid var(--line);
    border-radius: 20px;
    box-shadow: 0 30px 70px rgba(0, 0, 0, 0.45);
  }

  .logo {
    display: grid;
    place-items: center;
    width: 56px;
    height: 56px;
    font-size: 26px;
    background: var(--accent-dim);
    border: 1px solid rgba(255, 138, 61, 0.3);
    border-radius: 18px;
  }

  h1 { font-size: 21px; }

  p { margin: 0; line-height: 1.5; }

  .card button { width: 100%; }
</style>
