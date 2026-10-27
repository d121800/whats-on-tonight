<script setup>
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
const r = game.lastResult
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-4">
    <h2 class="text-lg font-bold">直播結束</h2>
    <div class="card flex flex-col gap-1 text-sm">
      <div>標題：{{ r.title }}</div>
      <div>訂閱人 +{{ r.statsDelta.subscribers }}</div>
      <div>short +{{ r.statsDelta.shorts }}</div>
      <div>人氣 +{{ r.statsDelta.popularity }}</div>
      <div>壓力 {{ r.stress }}</div>
      <div>體力 {{ r.staminaDelta }}</div>
      <div v-if="r.qte && r.qte.length">
        煞車 QTE：
        <span v-for="(x, i) in r.qte" :key="i" :class="x.success ? 'text-green-400' : 'text-red-400'">
          {{ x.success ? '成功' : '失控' }}{{ i < r.qte.length - 1 ? '、' : '' }}
        </span>
      </div>
    </div>
    <div class="card text-sm">{{ texts.evaluation[r.mood] }}</div>
    <button class="btn-primary mt-auto" @click="game.closeStreamResult()">End</button>
  </section>
</template>
