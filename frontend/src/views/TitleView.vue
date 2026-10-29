<script setup>
// 首頁：Start／繼續／Setting，下方三個圖示入口（關於、成就、生日）。難度選擇移到 Setting。
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import { loadPrefs } from '../game/save'

const router = useRouter()
const game = useGameStore()
const hasSave = ref(false)

onMounted(() => {
  hasSave.value = game.hasSave()
})

function start() {
  game.newGame(loadPrefs().difficulty)
  router.push({ name: 'game' })
}
function resume() {
  game.resume()
  router.push({ name: 'game' })
}
</script>

<template>
  <main class="flex-1 flex flex-col items-center justify-center gap-10 px-4 py-10">
    <!-- placeholder：標題 logo 待美術 -->
    <h1 class="text-4xl md:text-5xl font-bold text-brand text-center">今晚播什麼？</h1>

    <div class="w-full max-w-xs flex flex-col gap-3">
      <button class="btn-primary" @click="start">Start</button>
      <button v-if="hasSave" class="btn" @click="resume">繼續</button>
      <RouterLink class="btn" :to="{ name: 'settings' }">Setting</RouterLink>
    </div>

    <nav class="flex gap-4">
      <RouterLink :to="{ name: 'about' }" class="btn flex-col gap-0 w-20 py-2" title="關於">
        <span class="text-2xl">ℹ️</span><span class="text-xs text-white/70">關於</span>
      </RouterLink>
      <RouterLink :to="{ name: 'achievements' }" class="btn flex-col gap-0 w-20 py-2" title="成就">
        <span class="text-2xl">🏆</span><span class="text-xs text-white/70">成就</span>
      </RouterLink>
      <RouterLink :to="{ name: 'birthday' }" class="btn flex-col gap-0 w-20 py-2" title="生日">
        <span class="text-2xl">🎂</span><span class="text-xs text-white/70">生日</span>
      </RouterLink>
    </nav>
  </main>
</template>
