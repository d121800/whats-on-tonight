<script setup>
import { useGameStore } from '../stores/game'
import { CONFIG } from '../game/config'
const game = useGameStore()
</script>

<template>
  <header class="flex items-center justify-between px-4 py-2 bg-white/5 text-sm">
    <div>
      <span class="font-semibold">Day {{ game.day }}</span>
      <span class="text-white/60 ml-2">{{ game.dateLabel }}</span>
      <span class="text-white/60 ml-2">{{ game.clockLabel }}</span>
    </div>
    <div class="flex items-center gap-2">
      <span>體力</span>
      <div class="w-24 h-2 bg-white/10 rounded">
        <div class="h-2 bg-accent rounded" :style="{ width: Math.max(0, game.stamina) / CONFIG.stamina.max * 100 + '%' }" />
      </div>
      <span class="tabular-nums">{{ game.stamina }}</span>
    </div>
  </header>
</template>
