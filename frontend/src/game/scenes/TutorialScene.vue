<script setup>
import { ref } from 'vue'
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
const i = ref(0)
const lines = texts.tutorial

function next() {
  if (i.value < lines.length - 1) i.value++
  else game.finishTutorial()
}
</script>

<template>
  <section class="flex-1 flex flex-col justify-end p-4 gap-4">
    <!-- placeholder：阿邁小精靈立繪、房間轉場、聚焦螢幕演出待美術 -->
    <div class="flex-1 flex items-center justify-center text-6xl">🧚</div>
    <div class="card min-h-24" @click="next">
      <div class="text-xs text-brand mb-1">阿邁小精靈</div>
      <p>{{ lines[i] }}</p>
      <div class="text-right text-xs text-white/40 mt-2">{{ i + 1 }} / {{ lines.length }}　點擊繼續</div>
    </div>
    <button class="btn self-end text-sm" @click="game.finishTutorial()">Skip</button>
  </section>
</template>
