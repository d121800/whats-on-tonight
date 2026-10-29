<script setup>
// 結算（草圖 13 右）：標題框、數值框（short、訂閱人、壓力、體力）、評價氣泡＋阿萬頭像、End
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
const r = game.lastResult
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-4">
    <div class="rounded-lg bg-white/10 px-3 py-2 text-center font-semibold truncate">{{ r.title }}</div>

    <div class="card flex flex-col gap-1 text-sm">
      <div class="flex justify-between"><span>short</span><span class="tabular-nums">+{{ r.statsDelta.shorts }}</span></div>
      <div class="flex justify-between"><span>訂閱人</span><span class="tabular-nums">+{{ r.statsDelta.subscribers }}</span></div>
      <div class="flex justify-between"><span>人氣</span><span class="tabular-nums">+{{ r.statsDelta.popularity }}</span></div>
      <div class="flex justify-between"><span>壓力</span><span class="tabular-nums">{{ r.stress }}</span></div>
      <div class="flex justify-between"><span>體力</span><span class="tabular-nums" :class="r.staminaDelta < 0 ? 'text-red-300' : 'text-green-300'">{{ r.staminaDelta > 0 ? '+' : '' }}{{ r.staminaDelta }}</span></div>
      <div v-if="r.qte && r.qte.length" class="flex justify-between">
        <span>前額葉煞車</span>
        <span>
          <span v-for="(x, i) in r.qte" :key="i" :class="x.success ? 'text-green-400' : 'text-red-400'">
            {{ x.success ? '成功' : '失控' }}{{ i < r.qte.length - 1 ? '、' : '' }}
          </span>
        </span>
      </div>
    </div>

    <!-- 評價氣泡 + 阿萬頭像 -->
    <div class="flex items-end gap-2 mt-2">
      <div class="relative flex-1 card text-sm">
        {{ texts.evaluation[r.mood] }}
        <span class="absolute -right-2 bottom-3 w-0 h-0 border-y-8 border-y-transparent border-l-8 border-l-white/5" />
      </div>
      <!-- placeholder：阿萬頭像待美術 -->
      <div class="w-16 h-16 rounded-full bg-white/10 flex items-center justify-center text-4xl shrink-0">🧑‍⚕️</div>
    </div>

    <button class="btn-primary mt-auto" @click="game.closeStreamResult()">End</button>
  </section>
</template>
