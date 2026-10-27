<script setup>
// 煞車 QTE：標記在軌道上來回移動，點螢幕或按空白鍵停下，停在目標區內為成功。
// 只負責演出與判定，結果透過 done 事件交回，數值由 game store 處理。
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { CONFIG } from '../config'
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const emit = defineEmits(['done'])
const game = useGameStore()
const q = CONFIG.qte
const diff = game.difficultyCfg

const speed = q.markerSpeed * (diff.qteSpeedMul ?? 1)
const zoneWidth = Math.min(0.9, q.zoneWidth * (diff.qteZoneMul ?? 1))
const zoneStart = Math.random() * (1 - zoneWidth)
const zoneEnd = zoneStart + zoneWidth

const pos = ref(Math.random())        // 0~1，起點也隨機
const state = ref('running')          // running | success | fail | timeout
const timeLeft = ref(q.timeLimitSec)
let dir = Math.random() < 0.5 ? 1 : -1
let raf = null
let lastT = 0
let elapsed = 0

const inZone = computed(() => pos.value >= zoneStart && pos.value <= zoneEnd)
const message = computed(() => {
  if (state.value === 'success') return texts.qte.success
  if (state.value === 'fail') return texts.qte.fail
  if (state.value === 'timeout') return texts.qte.timeout
  return texts.qte.prompt
})

function frame(t) {
  if (state.value !== 'running') return
  if (!lastT) lastT = t
  const dt = Math.min(0.05, (t - lastT) / 1000)
  lastT = t
  elapsed += dt
  timeLeft.value = Math.max(0, q.timeLimitSec - elapsed)

  let p = pos.value + dir * speed * dt
  if (p > 1) { p = 2 - p; dir = -1 }
  if (p < 0) { p = -p; dir = 1 }
  pos.value = p

  if (elapsed >= q.timeLimitSec) {
    finish('timeout')
    return
  }
  raf = requestAnimationFrame(frame)
}

function press() {
  if (state.value !== 'running') return
  finish(inZone.value ? 'success' : 'fail')
}

function finish(result) {
  state.value = result
  cancelAnimationFrame(raf)
  setTimeout(() => emit('done', { success: result === 'success', reason: result }), q.feedbackMs)
}

function onKey(e) {
  if (e.code === 'Space' || e.key === ' ') {
    e.preventDefault()
    press()
  }
}

onMounted(() => {
  raf = requestAnimationFrame(frame)
  window.addEventListener('keydown', onKey)
})
onUnmounted(() => {
  cancelAnimationFrame(raf)
  window.removeEventListener('keydown', onKey)
})
</script>

<template>
  <div
    class="absolute inset-0 z-20 flex flex-col items-center justify-center gap-6 p-6 bg-black/70 select-none touch-none"
    @pointerdown.prevent="press"
  >
    <div class="text-lg font-bold text-center" :class="{ 'text-green-400': state === 'success', 'text-red-400': state === 'fail' || state === 'timeout' }">
      {{ message }}
    </div>

    <!-- 軌道 -->
    <div class="relative w-full max-w-sm h-8 rounded-full bg-white/15 overflow-hidden">
      <!-- 目標區 -->
      <div
        class="absolute top-0 bottom-0 bg-green-500/60"
        :style="{ left: zoneStart * 100 + '%', width: zoneWidth * 100 + '%' }"
      />
      <!-- 標記 -->
      <div
        class="absolute top-0 bottom-0 w-1.5 -ml-[3px] rounded"
        :class="state === 'running' ? 'bg-white' : (state === 'success' ? 'bg-green-300' : 'bg-red-400')"
        :style="{ left: pos * 100 + '%' }"
      />
    </div>

    <div class="text-xs text-white/60">
      <span v-if="state === 'running'">剩餘 {{ timeLeft.toFixed(1) }} 秒　點螢幕或按空白鍵</span>
      <span v-else>&nbsp;</span>
    </div>
  </div>
</template>
