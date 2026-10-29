<script setup>
// 直播畫面配置依草圖 14：
//   左欄：內容區（右上 Live 紅點 + 壓力圓形徽章）→ 標題列（右側小框放倒數）→ Day／日期／人數
//   右欄：聊天室在上、朋友頭像 + 三個直向指令在下
//   手機直式堆疊順序：內容、標題、資訊、指令、聊天室
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useGameStore } from '../../stores/game'
import { CONFIG } from '../../game/config'
import { api } from '../../api/client'
import friendsData from '../../data/friends.json'
import typesData from '../../data/stream-types.json'
import BrakeQte from '../qte/BrakeQte.vue'

const game = useGameStore()
const remaining = ref(CONFIG.stream.liveSeconds)
const chat = ref([])
let timer = null
let pool = []
let elapsed = 0

const moodIcon = { calm: '😐', happy: '😊', excited: '🤩' }
const moodLabel = { calm: '平淡', happy: '開心', excited: '激動' }

function friendIcon(id) {
  return friendsData.find((f) => f.id === id)?.icon ?? '❓'
}
const typeIcon = computed(() => typesData.find((t) => t.id === game.currentStream.type)?.icon ?? '')
const stressPct = computed(() => game.stress / CONFIG.stream.stressMax * 100)
// 壓力徽章顏色：依氣氛門檻 [暫定]
const stressColor = computed(() => {
  const t = CONFIG.stream.moodThresholds
  if (game.stress < t.calm) return 'border-green-400 text-green-300'
  if (game.stress < t.excited) return 'border-yellow-400 text-yellow-300'
  return 'border-red-500 text-red-400'
})
// 指令區頭像：第一位朋友，沒選朋友就放阿萬 placeholder
const commandAvatar = computed(() => (game.currentStream.friends[0] ? friendIcon(game.currentStream.friends[0]) : '🧑‍⚕️'))
// placeholder：人數暫用人氣推算
const viewers = computed(() => 10 + game.stats.popularity)

function tick() {
  // QTE 進行中：直播倒數暫停
  if (game.qteActive) return
  elapsed++
  remaining.value--
  if (remaining.value % 3 === 0 && pool.length) {
    chat.value.push(pool[Math.floor(Math.random() * pool.length)])
    if (chat.value.length > 30) chat.value.shift()
  }
  if (remaining.value <= 0) {
    clearInterval(timer)
    game.finishLive()
    return
  }
  game.maybeTriggerQte(elapsed)
}

function onQteDone(result) {
  game.resolveQte(result)
}

onMounted(async () => {
  const s = game.currentStream
  pool = await api.getLiveComments({ type: s.type, who: s.friends[0] ?? null })
  timer = setInterval(tick, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <section class="relative flex-1 flex flex-col">
    <div class="flex-1 flex flex-col md:flex-row">
      <!-- 左欄 -->
      <div class="flex-1 p-3 flex flex-col gap-2">
        <!-- 內容區 -->
        <div class="relative min-h-56 md:min-h-80 card overflow-hidden flex flex-col justify-end">
          <!-- 右上：Live 紅點 + 壓力徽章 -->
          <div class="absolute top-2 right-2 flex flex-col items-end gap-1">
            <div class="flex items-center gap-1 text-sm font-bold text-red-500">
              <span class="inline-block w-2.5 h-2.5 rounded-full bg-red-500 animate-pulse" />Live
            </div>
            <div
              class="w-12 h-12 rounded-full border-4 bg-ink/80 flex flex-col items-center justify-center leading-none transition-colors"
              :class="stressColor"
              title="壓力"
            >
              <span class="text-[10px] text-white/60">壓力</span>
              <span class="text-sm font-bold tabular-nums">{{ game.stress }}</span>
            </div>
          </div>

          <!-- 左上：關卡看板 placeholder（草圖 14「前額葉」看板） -->
          <div class="absolute top-2 left-2 w-28 h-24 md:w-40 md:h-32 rounded border-2 border-white/20 bg-white/5 flex flex-col items-center justify-center text-xs text-white/50">
            <span class="text-3xl">{{ moodIcon[game.mood] }}</span>
            <span>氣氛：{{ moodLabel[game.mood] }}</span>
            <span class="mt-1">{{ typeIcon }} 看板 placeholder</span>
          </div>

          <!-- 下排：動物朋友（左）＋阿萬（右）placeholder 立繪 -->
          <div class="flex items-end justify-between px-3 pb-1 pt-28 md:pt-36">
            <div class="text-3xl md:text-4xl flex gap-1">
              <span v-for="f in game.currentStream.friends" :key="f">{{ friendIcon(f) }}</span>
            </div>
            <div class="text-5xl md:text-7xl">🧑‍⚕️</div>
          </div>
        </div>

        <!-- 標題列：右側小框放倒數 -->
        <div class="flex items-center gap-2 rounded-lg bg-white/10 px-3 py-2 text-sm">
          <span class="flex-1 truncate font-semibold">{{ game.currentStream.title }}</span>
          <span class="rounded bg-ink/60 px-2 py-0.5 tabular-nums text-white/80">{{ remaining }}s</span>
        </div>

        <!-- Day／日期／人數 -->
        <div class="flex items-center gap-3 rounded-lg bg-white/5 px-3 py-1.5 text-xs text-white/70">
          <span>Day {{ game.day }}</span>
          <span>{{ game.dateLabel }}</span>
          <span class="ml-auto">人數 {{ viewers }}<span class="text-white/40">（placeholder）</span></span>
        </div>
      </div>

      <!-- 右欄：手機直式反過來（指令在上、聊天室在下），桌機聊天室在上 -->
      <aside class="md:w-64 flex flex-col-reverse md:flex-col border-t md:border-t-0 md:border-l border-white/10">
        <div class="flex-1 p-3 flex flex-col min-h-0">
          <div class="text-xs text-white/50 mb-2">聊天室</div>
          <ul class="flex-1 overflow-y-auto max-h-40 md:max-h-none flex flex-col gap-1.5 text-sm">
            <li v-for="(m, i) in chat" :key="i" class="flex items-start gap-2">
              <span class="shrink-0 w-5 h-5 rounded-full bg-white/20 text-[10px] flex items-center justify-center">{{ m.who ? friendIcon(m.who) : '👤' }}</span>
              <span class="leading-tight">{{ m.msg }}</span>
            </li>
          </ul>
        </div>

        <!-- 指令區：朋友頭像 + 三個直向按鈕 -->
        <div class="p-3 border-b md:border-b-0 md:border-t border-white/10 flex items-center gap-3">
          <div class="w-14 h-14 rounded-full bg-white/10 flex items-center justify-center text-3xl shrink-0">{{ commandAvatar }}</div>
          <div class="flex-1 flex flex-col gap-1.5">
            <button
              v-for="(c, key) in CONFIG.stream.commands"
              :key="key"
              class="btn text-sm py-1.5"
              :disabled="game.qteActive"
              @click="game.liveCommand(key)"
            >
              {{ c.label }}
            </button>
          </div>
        </div>
      </aside>
    </div>

    <!-- 壓力條（細）放最底，數值在徽章已顯示 -->
    <div class="h-1 bg-white/10">
      <div class="h-1 bg-red-400 transition-all" :style="{ width: stressPct + '%' }" />
    </div>

    <!-- QTE 疊層 -->
    <BrakeQte v-if="game.qteActive" @done="onQteDone" />
  </section>
</template>
