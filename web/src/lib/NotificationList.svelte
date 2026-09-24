<script>
  import { formatTime } from './api.js';

  let { notifications, showUser = false } = $props();
</script>

{#if notifications.length === 0}
  <p class="empty muted">Уведомлений пока нет.</p>
{:else}
  <ul>
    {#each notifications as notification (notification.notificationId)}
      <li>
        <p>{notification.text}</p>
        <span class="muted">
          {#if showUser}<b>{notification.userName}</b> · {/if}{formatTime(notification.createdAt)}
        </span>
      </li>
    {/each}
  </ul>
{/if}

<style>
  ul {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  li {
    position: relative;
    padding: 11px 14px 11px 18px;
    background: var(--panel-soft);
    border: 1px solid var(--line);
    border-radius: 10px;
    overflow: hidden;
  }

  li::before {
    content: '';
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    width: 3px;
    background: var(--accent);
    opacity: 0.7;
  }

  p {
    margin: 0 0 5px;
    font-size: 14px;
    line-height: 1.35;
  }

  b { font-weight: 600; color: #b9c1d1; }

  .empty {
    margin: 0;
    padding: 20px 0;
    text-align: center;
  }
</style>
