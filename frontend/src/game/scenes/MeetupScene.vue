<script setup>
// 約出來（草圖 5 右下）：兩個角色並肩一張圖＋一句話，按了之後套出門的時間與體力計算
import { ref } from 'vue'
import { useGameStore } from '../../stores/game'
import friends from '../../data/friends.json'
import texts from '../../data/texts.json'

const game = useGameStore()
const friend = friends.find((f) => f.id === game.currentMeetup?.friendId)
const result = ref(null)

function walk() {
  // goOut 內部會推進時間；若因此被強制睡覺／累倒，scene 已被接管，這裡不再顯示結果
  result.value = game.goOut()
}
function home() {
  game.endMeetup()
}
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-4">
    <!-- placeholder：雙人見面圖待美術 -->
    <div class="flex-1 min-h-48 card flex flex-col items-center justify-center gap-3">
      <div class="flex items-end gap-4 text-7xl">
        <span>{{ friend?.icon ?? '❓' }}</span>
        <span>🧑‍⚕️</span>
      </div>
      <p class="text-sm text-white/80 text-center">
        {{ friend?.name ?? '朋友' }}{{ texts.meetup.line }}
      </p>
      <p v-if="result" class="text-accent text-sm">出門 {{ result.hours }} 小時，體力 +{{ result.gain }}</p>
    </div>
    <button v-if="!result" class="btn-primary" @click="walk">一起出門走走</button>
    <button v-else class="btn-primary" @click="home">回家</button>
  </section>
</template>
