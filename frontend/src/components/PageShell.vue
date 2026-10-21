<script setup>
defineProps({ title: { type: String, required: true } })
</script>

<template>
  <main class="flex-1 flex flex-col px-4 py-6 gap-4">
    <header class="flex items-center gap-3">
      <RouterLink :to="{ name: 'title' }" class="text-white/60">←</RouterLink>
      <h1 class="text-xl font-bold">{{ title }}</h1>
    </header>
    <section class="flex-1">
      <slot />
    </section>
  </main>
</template>
