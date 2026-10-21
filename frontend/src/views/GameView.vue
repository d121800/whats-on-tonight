<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../stores/game'
import TutorialScene from '../game/scenes/TutorialScene.vue'
import RoomScene from '../game/scenes/RoomScene.vue'
import PhoneScene from '../game/scenes/PhoneScene.vue'
import StreamSetupScene from '../game/scenes/StreamSetupScene.vue'
import StreamLiveScene from '../game/scenes/StreamLiveScene.vue'
import StreamResultScene from '../game/scenes/StreamResultScene.vue'
import SleepScene from '../game/scenes/SleepScene.vue'
import CollapseScene from '../game/scenes/CollapseScene.vue'
import DayTransitionScene from '../game/scenes/DayTransitionScene.vue'
import BirthdayStreamScene from '../game/scenes/BirthdayStreamScene.vue'
import EndingScene from '../game/scenes/EndingScene.vue'

const sceneMap = {
  tutorial: TutorialScene,
  room: RoomScene,
  phone: PhoneScene,
  streamSetup: StreamSetupScene,
  streamLive: StreamLiveScene,
  streamResult: StreamResultScene,
  sleep: SleepScene,
  collapse: CollapseScene,
  dayTransition: DayTransitionScene,
  birthdayStream: BirthdayStreamScene,
  ending: EndingScene,
}

const router = useRouter()
const game = useGameStore()

onMounted(() => {
  // 直接開 /#/game 或重新整理時：有存檔就讀回來，沒有就回首頁
  if (game.hasSave()) game.resume()
  else router.replace({ name: 'title' })
})
</script>

<template>
  <main class="flex-1 flex flex-col">
    <component :is="sceneMap[game.scene]" />
    <!-- 開發用：顯示目前狀態 -->
    <div class="text-[10px] text-white/30 px-2 py-1 border-t border-white/10">
      scene={{ game.scene }} day={{ game.day }} clock={{ game.clockLabel }} stamina={{ game.stamina }} stress={{ game.stress }}
      <button class="ml-2 underline" @click="game.reset(); router.push({ name: 'title' })">reset</button>
    </div>
  </main>
</template>
