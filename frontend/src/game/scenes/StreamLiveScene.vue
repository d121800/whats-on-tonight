<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useGameStore } from '../../stores/game'
import { CONFIG } from '../../game/config'
import { api } from '../../api/client'
import friendsData from '../../data/friends.json'

const game = useGameStore()
const remaining = ref(CONFIG.stream.liveSeconds)
const chat = ref([])
let timer = null
let pool = []

const moodIcon = { calm: '😐', happy: '😊', excited: '🤩' }
const moodLabel = { calm: '平淡', happy: '開心', excited: '激動' }

function friendIcon(id) {
  return friendsData.find((f) => f.id === id)?.icon ?? '❓'
}

onMounted(async () => {
  const s = game.currentStream
  pool = await api.getLiveComments({ type: s.type, who: s.friends[0] ?? null })
  timer = setInterval(() => {
    remaining.value--
    if (remaining.value % 3 === 0 && pool.length) {
      chat.value.push(pool[Math.floor(Math.random() * pool.length)])
      if (chat.value.length > 30) chat.value.shift()
    }
    if (remaining.value <= 0) {
      clearInterval(timer)
      game.finishLive()
    }
  }, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <section class="flex-1 flex flex-col">
    <!-- 手機直式：上為直播畫面、下為聊天室；桌機改為左右 -->
    <div class="flex-1 flex flex-col md:flex-row">
      <div class="flex-1 p-4 flex flex-col gap-2">
        <div class="flex items-center gap-2 text-sm">
          <span class="inline-block w-2 h-2 rounded-full bg-red-500 animate-pulse" /> LIVE
          <span class="ml-auto tabular-nums">{{ remaining }}s</span>
        </div>
        <!-- placeholder：阿萬立繪、動物朋友、關卡演出待美術與規則 -->
        <div class="flex-1 min-h-40 card flex flex-col items-center justify-center gap-2">
          <div class="text-5xl">{{ moodIcon[game.mood] }}</div>
          <div class="text-sm text-white/60">氣氛：{{ moodLabel[game.mood] }}</div>
          <div class="text-2xl">
            <span v-for="f in game.currentStream.friends" :key="f">{{ friendIcon(f) }}</span>
          </div>
        </div>
        <div class="text-sm">
          壓力
          <div class="h-2 bg-white/10 rounded">
            <div class="h-2 bg-red-400 rounded transition-all" :style="{ width: game.stress / CONFIG.stream.stressMax * 100 + '%' }" />
          </div>
        </div>
        <div class="grid grid-cols-3 gap-2">
          <button v-for="(c, key) in CONFIG.stream.commands" :key="key" class="btn text-sm" @click="game.liveCommand(key)">
            {{ c.label }}
          </button>
        </div>
      </div>

      <aside class="md:w-64 border-t md:border-t-0 md:border-l border-white/10 p-3 flex flex-col">
        <div class="text-xs text-white/50 mb-2">聊天室</div>
        <ul class="flex-1 overflow-y-auto max-h-48 md:max-h-none flex flex-col gap-1 text-sm">
          <li v-for="(m, i) in chat" :key="i">{{ m.msg }}</li>
        </ul>
      </aside>
    </div>
    <footer class="px-4 py-2 text-xs text-white/60 border-t border-white/10 flex justify-between">
      <span>{{ game.currentStream.title }}</span>
      <span>{{ game.dateLabel }}　觀看 {{ 10 + game.stats.popularity }}（placeholder）</span>
    </footer>
  </section>
</template>
