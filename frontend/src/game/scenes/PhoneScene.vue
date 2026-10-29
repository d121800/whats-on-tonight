<script setup>
import { ref } from 'vue'
import { useGameStore } from '../../stores/game'
import posts from '../../data/social-posts.json'
import chats from '../../data/line-chats.json'

const game = useGameStore()
const tab = ref('social') // social | line
const openChat = ref(null)

// 進入手機已由 store.openPhone() 扣 30 分鐘（草圖 2）
function meetup(chat) {
  // [手稿] 聊天可「約出來」→ 雙人見面事件；見面畫面待做（第三批），暫以出門處理
  // 出門唯一入口在這裡：房間沒有獨立出門鈕（hannah 2026-10-28）
  game.goOut()
  openChat.value = null
  if (game.scene === 'phone') game.goto('room')
}
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-3">
    <header class="flex items-center justify-between">
      <div class="flex gap-2">
        <button class="btn text-sm" :class="{ 'bg-brand': tab === 'social' }" @click="tab = 'social'; openChat = null">🐻 Bear 社群</button>
        <button class="btn text-sm" :class="{ 'bg-brand': tab === 'line' }" @click="tab = 'line'">💬 賴</button>
      </div>
      <button class="text-white/60" @click="game.goto('room')">關閉</button>
    </header>

    <!-- 社群 -->
    <ul v-if="tab === 'social'" class="flex flex-col gap-2">
      <li v-for="p in posts" :key="p.id" class="card">
        <div class="text-sm font-semibold">{{ p.avatar }} {{ p.author }}</div>
        <p class="text-sm mt-1">{{ p.text }}</p>
        <div v-if="p.image" class="mt-2 h-24 bg-white/10 rounded" />
      </li>
    </ul>

    <!-- Line 列表 -->
    <ul v-else-if="!openChat" class="flex flex-col gap-2">
      <li v-for="c in chats" :key="c.id" class="card cursor-pointer" @click="openChat = c">
        {{ c.isGroup ? '👥' : '🙂' }} {{ c.name }}
      </li>
    </ul>

    <!-- 聊天室 -->
    <div v-else class="flex-1 flex flex-col gap-2">
      <div class="text-sm text-white/60">
        <button @click="openChat = null">← </button>{{ openChat.name }}
      </div>
      <div class="flex-1 flex flex-col gap-2">
        <div v-for="(m, i) in openChat.messages" :key="i" class="self-start card text-sm max-w-[80%]">
          <span class="text-xs text-white/50">{{ m.from }}</span><br />{{ m.text }}
        </div>
      </div>
      <button v-if="!openChat.isGroup" class="btn-primary" @click="meetup(openChat)">約出來（暫以出門處理）</button>
    </div>
  </section>
</template>
