<script setup>
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
</script>

<template>
  <section class="flex-1 flex flex-col items-center justify-center p-4 gap-4 text-center">
    <div class="text-sm text-white/60">{{ game.transitionNote }}</div>
    <h2 class="text-2xl font-bold">Day {{ game.day }}　{{ game.dateLabel }}</h2>
    <p v-if="game.isBirthday" class="text-accent">{{ texts.birthday }}</p>
    <p v-else class="text-sm text-white/60">上班一整天，19:00 到家。</p>
    <button class="btn-primary" @click="game.goto('room')">回家</button>
  </section>
</template>
