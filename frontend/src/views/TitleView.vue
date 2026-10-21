<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'

const router = useRouter()
const game = useGameStore()
const hasSave = ref(false)
const difficulty = ref('normal')

onMounted(() => {
  hasSave.value = game.hasSave()
})

function start() {
  game.newGame(difficulty.value)
  router.push({ name: 'game' })
}
function resume() {
  game.resume()
  router.push({ name: 'game' })
}
</script>

<template>
  <main class="flex-1 flex flex-col items-center justify-center gap-8 px-4 py-10">
    <!-- placeholder：標題 logo 待美術 -->
    <h1 class="text-4xl md:text-5xl font-bold text-brand text-center">今晚播什麼？</h1>

    <div class="w-full max-w-xs flex flex-col gap-3">
      <div class="flex gap-2 justify-center text-sm">
        <label class="flex items-center gap-1">
          <input v-model="difficulty" type="radio" value="normal" /> 普通
        </label>
        <label class="flex items-center gap-1">
          <input v-model="difficulty" type="radio" value="waha" /> 瓦哈（極困難）
        </label>
      </div>
      <button class="btn-primary" @click="start">Start</button>
      <button v-if="hasSave" class="btn" @click="resume">繼續</button>
      <RouterLink class="btn" :to="{ name: 'settings' }">Setting</RouterLink>
    </div>

    <nav class="flex gap-6 text-sm text-white/70">
      <RouterLink :to="{ name: 'about' }">關於</RouterLink>
      <RouterLink :to="{ name: 'achievements' }">成就</RouterLink>
      <RouterLink :to="{ name: 'birthday' }">生日</RouterLink>
    </nav>
  </main>
</template>
