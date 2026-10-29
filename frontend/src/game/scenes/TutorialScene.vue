<script setup>
// 教學三段（草圖 2「第一部：阿邁教學」）：
//   intro  訊息畫面，阿邁自我介紹
//   room   房間裡阿邁站在桌邊講解，底部對話框
//   focus  暗幕聚焦螢幕，點螢幕 → 開播準備
// 右下角橘色 Skip 隨時可跳過 → 一樣進開播準備。
import { ref, computed } from 'vue'
import { useGameStore } from '../../stores/game'
import RoomInfoPanel from '../../components/RoomInfoPanel.vue'
import RoomStage from '../../components/RoomStage.vue'
import texts from '../../data/texts.json'

const game = useGameStore()
const stage = ref('intro') // intro | room | focus
const i = ref(0)

const lines = computed(() => (stage.value === 'intro' ? texts.tutorial.intro : texts.tutorial.room))

function next() {
  if (i.value < lines.value.length - 1) {
    i.value++
    return
  }
  i.value = 0
  if (stage.value === 'intro') stage.value = 'room'
  else if (stage.value === 'room') stage.value = 'focus'
}
</script>

<template>
  <div class="flex-1 flex flex-col relative">
  <!-- 第一段：訊息畫面 -->
  <section v-if="stage === 'intro'" class="flex-1 flex flex-col justify-end p-4 gap-4">
    <!-- placeholder：阿邁小精靈立繪待美術 -->
    <div class="flex-1 flex items-center justify-center text-7xl">🧚</div>
    <div class="card min-h-24 cursor-pointer" @click="next">
      <div class="text-xs text-brand mb-1">阿邁小精靈</div>
      <p>{{ lines[i] }}</p>
      <div class="text-right text-xs text-white/40 mt-2">{{ i + 1 }} / {{ lines.length }}　點擊繼續</div>
    </div>
  </section>

  <!-- 第二、三段：在房間裡 -->
  <template v-else>
    <RoomInfoPanel />
    <section class="flex-1 flex flex-col relative">
      <RoomStage
        :interactive="false"
        :spotlight="stage === 'focus'"
        show-fairy
        @monitor="game.finishTutorial()"
      />

      <!-- 第二段對話框 -->
      <div v-if="stage === 'room'" class="absolute left-3 right-3 bottom-20 card cursor-pointer shadow-lg" @click="next">
        <div class="text-xs text-brand mb-1">阿邁小精靈</div>
        <p>{{ lines[i] }}</p>
        <div class="text-right text-xs text-white/40 mt-2">{{ i + 1 }} / {{ lines.length }}　點擊繼續</div>
      </div>

      <!-- 第三段提示（在暗幕之上） -->
      <p v-else class="absolute left-0 right-0 top-6 z-30 text-center text-accent font-semibold drop-shadow pointer-events-none">
        {{ texts.tutorial.focus }}
      </p>
    </section>
  </template>

  <!-- 橘色 Skip（草圖：右下角） -->
  <button class="absolute right-4 bottom-16 z-40 rounded-full bg-accent text-ink font-bold px-4 py-2 shadow-lg" @click="game.skipTutorial()">
    Skip
  </button>
  </div>
</template>
