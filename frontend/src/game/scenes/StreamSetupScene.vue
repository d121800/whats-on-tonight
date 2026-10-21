<script setup>
import { ref, computed } from 'vue'
import { useGameStore } from '../../stores/game'
import { CONFIG } from '../../game/config'
import types from '../../data/stream-types.json'
import friends from '../../data/friends.json'

const game = useGameStore()
const title = ref('')
const type = ref(types[0].id)
const picked = ref([])
const hours = ref(CONFIG.stream.hoursMin)

const hourOptions = computed(() => {
  const arr = []
  for (let h = CONFIG.stream.hoursMin; h <= CONFIG.stream.hoursMax; h++) arr.push(h)
  return arr
})

function toggle(id) {
  const i = picked.value.indexOf(id)
  if (i >= 0) picked.value.splice(i, 1)
  else picked.value.push(id)
}
function start() {
  game.startStream({
    title: title.value.trim() || '未命名直播',
    type: type.value,
    friends: [...picked.value],
    hours: hours.value,
  })
}
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-4">
    <h2 class="text-lg font-bold">開播設定</h2>

    <label class="flex flex-col gap-1 text-sm">
      直播標題
      <input v-model="title" class="input" placeholder="自填標題" />
    </label>

    <div class="text-sm">
      直播 type
      <div class="grid grid-cols-3 gap-2 mt-1">
        <button v-for="t in types" :key="t.id" class="btn text-sm" :class="{ 'bg-brand': type === t.id }" @click="type = t.id">
          {{ t.icon }} {{ t.name }}
        </button>
      </div>
    </div>

    <div class="text-sm">
      朋友／參與者（可不選）
      <div class="flex flex-wrap gap-2 mt-1">
        <button v-for="f in friends" :key="f.id" class="btn text-sm" :class="{ 'bg-brand': picked.includes(f.id) }" @click="toggle(f.id)">
          {{ f.icon }} {{ f.name }}
        </button>
      </div>
    </div>

    <label class="flex items-center gap-2 text-sm">
      預計直播時數
      <select v-model.number="hours" class="input w-24">
        <option v-for="h in hourOptions" :key="h" :value="h">{{ h }} 小時</option>
      </select>
    </label>

    <div class="mt-auto grid grid-cols-2 gap-2">
      <button class="btn" @click="game.goto('room')">取消</button>
      <button class="btn-primary" @click="start">開始直播</button>
    </div>
  </section>
</template>
