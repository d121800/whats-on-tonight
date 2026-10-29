<script setup>
import { ref } from 'vue'
import { useGameStore } from '../stores/game'
import { CONFIG } from '../game/config'
import { exportSave, loadSave, clearSave, loadPrefs, writePrefs } from '../game/save'
import PageShell from '../components/PageShell.vue'

const game = useGameStore()
const difficulty = ref(loadPrefs().difficulty)

function setDifficulty(key) {
  difficulty.value = key
  writePrefs({ difficulty: key })
}
function download() {
  const s = loadSave()
  if (s) exportSave(s)
}
function wipe() {
  if (confirm('確定要清除本機存檔？')) {
    clearSave()
    game.reset()
  }
}
</script>

<template>
  <PageShell title="Setting">
    <!-- 難度從首頁移到這裡（草圖首頁沒有難度選擇）；下一局 Start 時生效 -->
    <div class="text-sm">
      等級
      <div class="flex gap-2 mt-1">
        <button
          v-for="(d, key) in CONFIG.difficulty" :key="key"
          class="btn text-sm"
          :class="{ 'bg-brand': difficulty === key }"
          @click="setDifficulty(key)"
        >
          {{ d.label }}<span v-if="key === 'waha'" class="text-xs text-white/60">（極困難）</span>
        </button>
      </div>
      <p class="text-xs text-white/40 mt-1">下一次按 Start 時生效。</p>
    </div>

    <!-- 手稿未列其他 Setting 內容；以下為 placeholder -->
    <p class="text-white/60 text-sm mt-6">（placeholder）音量、語言等設定待規劃。</p>
    <div class="flex flex-col gap-2 mt-4">
      <button class="btn" @click="download">下載紀錄（JSON）</button>
      <button class="btn" disabled>上傳紀錄（待做）</button>
      <button class="btn text-red-300" @click="wipe">清除本機存檔</button>
    </div>
  </PageShell>
</template>
