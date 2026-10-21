<script setup>
import achievements from '../data/achievements.json'
import PageShell from '../components/PageShell.vue'
</script>

<template>
  <PageShell title="成就">
    <ul class="flex flex-col gap-2">
      <li v-for="a in achievements" :key="a.id" class="card">
        <div class="font-semibold">{{ a.name }}</div>
        <div class="text-sm text-white/60">{{ a.desc }}</div>
      </li>
    </ul>
    <p class="text-xs text-white/40 mt-4">解鎖狀態尚未實作；清單為草案。</p>
  </PageShell>
</template>
