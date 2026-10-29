<script setup>
// 房間右上角資訊面板（草圖 2）：第幾日、日期、時間、直播預告、體力
import { useGameStore } from '../stores/game'
import { CONFIG } from '../game/config'
const game = useGameStore()
</script>

<template>
  <header class="flex justify-end px-3 pt-3">
    <div class="card text-sm w-52 md:w-60 flex flex-col gap-1">
      <div class="flex items-baseline justify-between">
        <span class="font-semibold">第 {{ game.day }} 日</span>
        <span class="text-white/70">{{ game.dateLabel }}</span>
        <span class="text-white/70 tabular-nums">{{ game.clockLabel }}</span>
      </div>
      <div class="text-white/80">
        <span v-if="game.isBirthday" class="text-accent">🎂 今天是生日直播！</span>
        <span v-else>直播預告：自選時間（placeholder）</span>
      </div>
      <div class="flex items-center gap-2">
        <span>體力</span>
        <div class="flex-1 h-2 bg-white/10 rounded">
          <div class="h-2 bg-accent rounded transition-all" :style="{ width: Math.max(0, game.stamina) / CONFIG.stamina.max * 100 + '%' }" />
        </div>
        <span class="tabular-nums w-8 text-right">{{ game.stamina }}</span>
      </div>
    </div>
  </header>
</template>
