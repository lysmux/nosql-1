<script>
  let { title, onClose, children } = $props();
</script>

<div
  class="backdrop"
  role="presentation"
  onclick={(e) => e.target === e.currentTarget && onClose()}
>
  <div class="window">
    <header>
      <h2>{title}</h2>
      <button class="close" onclick={onClose}>✕</button>
    </header>
    {@render children()}
  </div>
</div>

<svelte:window onkeydown={(e) => e.key === 'Escape' && onClose()} />

<style>
  .backdrop {
    position: fixed;
    z-index: 50;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    background: rgba(6, 8, 12, 0.7);
    backdrop-filter: blur(3px);
  }

  .window {
    display: flex;
    flex-direction: column;
    gap: 16px;
    width: 440px;
    max-height: 82vh;
    overflow-y: auto;
    padding: 24px;
    background: var(--panel);
    border: 1px solid var(--line);
    border-radius: 18px;
    box-shadow: 0 24px 60px rgba(0, 0, 0, 0.5);
  }

  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  h2 { font-size: 19px; }

  .close {
    padding: 4px 10px;
    color: var(--muted);
    background: transparent;
    border-color: transparent;
  }

  .close:hover { color: var(--text); }
</style>
