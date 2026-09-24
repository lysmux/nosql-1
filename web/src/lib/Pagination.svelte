<script>
  let { page, totalPages, onChange } = $props();
</script>

{#if totalPages > 1}
  <nav>
    <button disabled={page === 0} onclick={() => onChange(page - 1)}>← Назад</button>
    <span class="muted">Стр. {page + 1} из {totalPages}</span>
    <button disabled={page >= totalPages - 1} onclick={() => onChange(page + 1)}>Вперёд →</button>
  </nav>
{/if}

<style>
  nav {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-top: 18px;
  }
</style>
