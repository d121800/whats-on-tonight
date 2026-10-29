<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../../stores/game'
import RoomInfoPanel from '../../components/RoomInfoPanel.vue'
import RoomStage from '../../components/RoomStage.vue'
import texts from '../../data/texts.json'

const game = useGameStore()
const router = useRouter()
const toast = ref('')
let toastTimer = null

function say(msg) {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 1800)
}
function onMonitor() {
  if (game.isBirthday) game.startBirthdayStream()
  else game.goto('streamSetup')
}
function petCat() {
  const r = game.petCat()
  say(r.refused ? texts.catRefuse : `吸貓 30 分鐘，體力 +${r.gain}`)
}
</script>

<template>
  <div class="flex-1 flex flex-col">
  <RoomInfoPanel />
  <section class="flex-1 flex flex-col relative">
    <RoomStage
      :birthday="game.isBirthday"
      :phone-badge="game.phoneUnread"
      @monitor="onMonitor"
      @chat="say(texts.chatIcon)"
      @cat="petCat"
      @phone="game.openPhone()"
      @sleep="game.goto('sleep')"
      @gear="router.push({ name: 'settings' })"
    />
    <!-- 出門沒有獨立按鈕：只能從手機 Line 約朋友（hannah 2026-10-28） -->
    <p v-if="toast" class="absolute left-0 right-0 bottom-16 text-center text-accent text-sm pointer-events-none drop-shadow">
      {{ toast }}
    </p>
  </section>
  </div>
</template>
