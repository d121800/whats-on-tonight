<script setup>
// 成就清單（草圖 7）：四個成就 + 進度條。解鎖與進度尚未實作，進度先全部 0。
import achievements from '../data/achievements.json'
import PageShell from '../components/PageShell.vue'
</script>

<template>
  <PageShell title="成就">
    <ul class="flex flex-col gap-2">
      <li v-for="a in achievements" :key="a.id" class="card">
        <div class="flex items-center justify-between">
          <span class="font-semibold">{{ a.name }}</span>
          <span class="text-xs text-white/40">0 / 1</span>
        </div>
        <div class="text-sm text-white/60">{{ a.desc }}</div>
        <div class="h-1.5 mt-2 bg-white/10 rounded">
          <div class="h-1.5 bg-accent rounded" style="width: 0%" />
        </div>
      </li>
    </ul>
    <p class="text-xs text-white/40 mt-4">解鎖條件與進度尚未實作；名稱取自草圖 7。</p>
  </PageShell>
</template>
