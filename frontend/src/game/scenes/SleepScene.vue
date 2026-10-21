<script setup>
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
</script>

<template>
  <section class="flex-1 flex flex-col items-center justify-center p-4 gap-4 text-center">
    <div class="text-5xl">🛏️</div>
    <p v-if="game.pendingSleepForced" class="text-accent">{{ texts.forceSleep }}</p>
    <p class="text-sm text-white/60">現在 {{ game.clockLabel }}，要睡了嗎？睡覺時間決定明天的體力。</p>
    <div class="flex gap-2">
      <!-- [手稿] 03:00 是否可拒絕尚未說明；暫定強制時不可取消 -->
      <button v-if="!game.pendingSleepForced" class="btn" @click="game.goto('room')">再撐一下</button>
      <button class="btn-primary" @click="game.sleep()">睡覺</button>
    </div>
  </section>
</template>
