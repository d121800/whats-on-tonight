<script setup>
import { useGameStore } from '../stores/game'
import { exportSave, loadSave, clearSave } from '../game/save'
import PageShell from '../components/PageShell.vue'

const game = useGameStore()

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
    <!-- 手稿未列 Setting 內容；以下為 placeholder -->
    <p class="text-white/60 text-sm">（placeholder）音量、語言等設定待規劃。</p>
    <div class="flex flex-col gap-2 mt-4">
      <button class="btn" @click="download">下載紀錄（JSON）</button>
      <button class="btn" disabled>上傳紀錄（待做）</button>
      <button class="btn text-red-300" @click="wipe">清除本機存檔</button>
    </div>
  </PageShell>
</template>
