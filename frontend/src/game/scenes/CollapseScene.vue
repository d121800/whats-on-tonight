<script setup>
import { useGameStore } from '../../stores/game'
import texts from '../../data/texts.json'

const game = useGameStore()
</script>

<template>
  <section class="flex-1 flex flex-col items-center justify-center p-4 gap-4 text-center">
    <div class="text-5xl">😵</div>
    <p>{{ texts.collapse }}</p>
    <button class="btn-primary" @click="game.collapse()">……</button>
  </section>
</template>
