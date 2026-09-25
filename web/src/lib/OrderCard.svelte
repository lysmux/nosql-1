<script>
  import { STATUSES, STATUS_LABELS, formatTime } from './api.js';

  let { order, onOpenUser, onAdvance } = $props();

  let current = $derived(STATUSES.indexOf(order.status));
  let finished = $derived(order.status === 'DELIVERED');
</script>

<article>
  <header>
    <h3>{order.restaurantName}</h3>
    <span class="amount">{order.totalAmount.toLocaleString('ru-RU')} ₽</span>
  </header>

  <div class="meta">
    <button class="user" onclick={() => onOpenUser(order.userId)}>
      <span class="avatar">{order.userName.slice(0, 1).toUpperCase()}</span>
      {order.userName}
    </button>
    <span class="muted">{formatTime(order.createdAt)}</span>
  </div>

  <ol class="pipeline">
    {#each STATUSES as status, index (status)}
      <li class:done={index < current} class:current={index === current}>
        <span class="dot">{index < current ? '✓' : ''}</span>
        <span class="label">{STATUS_LABELS[status]}</span>
      </li>
    {/each}
  </ol>

  <button class:primary={!finished} disabled={finished} onclick={() => onAdvance(order.orderId)}>
    {finished ? 'Заказ доставлен' : `Перевести в «${STATUS_LABELS[STATUSES[current + 1]]}»`}
  </button>
</article>

<style>
  article {
    display: flex;
    flex-direction: column;
    gap: 14px;
    padding: 18px;
    background: linear-gradient(180deg, var(--panel), #14171f);
    border: 1px solid var(--line);
    border-radius: var(--radius);
    transition: border-color 0.15s, transform 0.15s;
  }

  article:hover {
    border-color: #3a4353;
    transform: translateY(-1px);
  }

  header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 12px;
  }

  h3 { font-size: 17px; }

  .amount {
    font-weight: 600;
    color: var(--accent);
    white-space: nowrap;
  }

  .meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-top: -6px;
  }

  .user {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 12px 4px 4px;
    font-size: 13px;
    background: transparent;
    border-radius: 999px;
  }

  .user:hover { background: var(--accent-dim); border-color: var(--accent); }

  .avatar {
    display: grid;
    place-items: center;
    width: 22px;
    height: 22px;
    font-size: 11px;
    font-weight: 600;
    color: #221507;
    background: linear-gradient(180deg, #ffa669, var(--accent));
    border-radius: 50%;
  }

  .pipeline {
    display: flex;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .pipeline li {
    position: relative;
    isolation: isolate;
    display: flex;
    flex: 1;
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }

  .pipeline li::before {
    content: '';
    position: absolute;
    top: 9px;
    right: 50%;
    left: -50%;
    height: 2px;
    background: var(--line);
  }

  .pipeline li:first-child::before { display: none; }
  .pipeline li.done::before,
  .pipeline li.current::before { background: var(--accent); }

  .dot {
    display: grid;
    place-items: center;
    z-index: 1;
    width: 20px;
    height: 20px;
    font-size: 11px;
    color: #221507;
    background: var(--bg);
    border: 2px solid var(--line);
    border-radius: 50%;
  }

  li.done .dot,
  li.current .dot {
    background: var(--accent);
    border-color: var(--accent);
  }

  li.current .dot { box-shadow: 0 0 0 4px var(--accent-dim); }

  .label {
    font-size: 11px;
    color: var(--muted);
    text-align: center;
  }

  li.done .label,
  li.current .label { color: var(--text); }
</style>
