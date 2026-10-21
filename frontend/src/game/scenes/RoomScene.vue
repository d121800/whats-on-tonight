<script setup>
import { ref } from 'vue'
import { useGameStore } from '../../stores/game'
import StatusBar from '../../components/StatusBar.vue'
import texts from '../../data/texts.json'

const game = useGameStore()
const toast = ref('')

function say(msg) {
  toast.value = msg
  setTimeout(() => (toast.value = ''), 1800)
}
function petCat() {
  const r = game.petCat()
  say(r.refused ? texts.catRefuse : `吸貓 30 分鐘，體力 +${r.gain}`)
}
function goOut() {
  const r = game.goOut()
  say(`出門 ${r.hours} 小時，體力 +${r.gain}`)
}
</script>

<template>
  <StatusBar />
  <section class="flex-1 flex flex-col p-4 gap-4">
    <div class="text-sm text-white/70">
      直播預告：<span v-if="game.isBirthday" class="text-accent">今天是生日直播！</span><span v-else>自選時間（placeholder）</span>
    </div>

    <!-- placeholder：房間圖（電腦桌、螢幕、主機、杯子、角色、動物）待美術 -->
    <div class="flex-1 min-h-48 card flex items-center justify-center text-white/40">
      房間圖 placeholder
    </div>

    <p v-if="toast" class="text-center text-accent text-sm">{{ toast }}</p>

    <div class="grid grid-cols-2 gap-2">
      <button v-if="game.isBirthday" class="btn-primary col-span-2" @click="game.startBirthdayStream()">🎂 開始生日直播</button>
      <button v-else class="btn-primary col-span-2" @click="game.goto('streamSetup')">🖥️ 電腦：開播</button>
      <button class="btn" @click="petCat">🐱 吸貓（30 分）</button>
      <button class="btn" @click="goOut">🚶 出門（1～3 小時）</button>
      <button class="btn" @click="game.goto('phone')">📱 手機</button>
      <button class="btn" @click="game.goto('sleep')">🛏️ 睡覺</button>
    </div>
    <!-- 底部圖示（對話／終端、齒輪）功能手稿未註明，暫不放 -->
  </section>
</template>
