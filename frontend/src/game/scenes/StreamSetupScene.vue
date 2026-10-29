<script setup>
// 開播準備（草圖 13 左）：名稱、type 圖示、人（🚫 不選＋五位朋友）、預計時數 1~4、開始
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
    <h2 class="text-lg font-bold">開播準備</h2>

    <label class="flex items-center gap-2 text-sm">
      <span class="shrink-0">名稱</span>
      <input v-model="title" class="input flex-1" placeholder="自填直播名稱" />
    </label>

    <div class="text-sm">
      type
      <div class="grid grid-cols-6 gap-1 mt-1">
        <button
          v-for="t in types" :key="t.id"
          class="btn flex-col gap-0 px-1 py-2"
          :class="{ 'bg-brand': type === t.id }"
          :title="t.name"
          @click="type = t.id"
        >
          <span class="text-2xl">{{ t.icon }}</span>
          <span class="text-[10px] text-white/70">{{ t.name }}</span>
        </button>
      </div>
    </div>

    <div class="text-sm">
      人
      <div class="grid grid-cols-6 gap-1 mt-1">
        <button class="btn flex-col gap-0 px-1 py-2" :class="{ 'bg-brand': picked.length === 0 }" title="不找人" @click="picked = []">
          <span class="text-2xl">🚫</span>
          <span class="text-[10px] text-white/70">不找人</span>
        </button>
        <button
          v-for="f in friends" :key="f.id"
          class="btn flex-col gap-0 px-1 py-2"
          :class="{ 'bg-brand': picked.includes(f.id) }"
          :title="f.name"
          @click="toggle(f.id)"
        >
          <span class="text-2xl">{{ f.icon }}</span>
          <span class="text-[10px] text-white/70 truncate w-full">{{ f.name }}</span>
        </button>
      </div>
    </div>

    <div class="mt-auto flex items-center gap-2 text-sm">
      預計
      <select v-model.number="hours" class="input w-24">
        <option v-for="h in hourOptions" :key="h" :value="h">{{ h }} hr</option>
      </select>
      <button class="btn ml-auto" @click="game.goto('room')">取消</button>
      <button class="btn-primary" @click="start">開始</button>
    </div>
  </section>
</template>
