<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useGameStore } from '../../stores/game'
import { CONFIG } from '../../game/config'
import friends from '../../data/friends.json'

// [手稿] 生日直播：蛋糕、動物朋友、阿萬、聊天室。是否可操作指令 [待定]，暫為純演出倒數。
const game = useGameStore()
const remaining = ref(Math.min(15, CONFIG.stream.liveSeconds)) // [暫定] 演出縮短為 15 秒
let timer = null

onMounted(() => {
  timer = setInterval(() => {
    remaining.value--
    if (remaining.value <= 0) {
      clearInterval(timer)
      game.finishLive()
    }
  }, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<template>
  <section class="flex-1 flex flex-col items-center justify-center p-4 gap-4 text-center">
    <div class="text-6xl">🎂</div>
    <div class="text-3xl"><span v-for="f in friends" :key="f.id">{{ f.icon }}</span></div>
    <p>生日直播中……{{ remaining }}s</p>
    <!-- placeholder：聊天室與生日 CG 待美術 -->
  </section>
</template>
