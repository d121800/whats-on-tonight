<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '../../stores/game'
import { api } from '../../api/client'
import texts from '../../data/texts.json'

// [手稿] 結算 → CG → 填名字與生日留言 → 製作名單（可 skip）→ 回首頁
const router = useRouter()
const game = useGameStore()
const step = ref('summary') // summary | cg | message | credits
const name = ref('')
const msg = ref('')
const sending = ref(false)
const sent = ref(false)

async function submit() {
  sending.value = true
  try {
    await api.postBirthMsg({ name: name.value.trim(), msg: msg.value.trim() })
    sent.value = true
  } finally {
    sending.value = false
  }
  step.value = 'credits'
}
function goHome() {
  game.finishGame()
  router.push({ name: 'title' })
}
</script>

<template>
  <section class="flex-1 flex flex-col p-4 gap-4">
    <template v-if="step === 'summary'">
      <h2 class="text-lg font-bold">最終結算</h2>
      <div class="card text-sm flex flex-col gap-1">
        <div>訂閱人：{{ game.stats.subscribers }}</div>
        <div>short：{{ game.stats.shorts }}</div>
        <div>人氣：{{ game.stats.popularity }}</div>
        <div>是否累倒過：{{ game.collapsed ? '是' : '否' }}</div>
      </div>
      <button class="btn-primary mt-auto" @click="step = 'cg'">下一步</button>
    </template>

    <template v-else-if="step === 'cg'">
      <!-- placeholder：CG 待美術 -->
      <div class="flex-1 card flex items-center justify-center text-white/40">CG placeholder</div>
      <button class="btn-primary" @click="step = 'message'">下一步</button>
    </template>

    <template v-else-if="step === 'message'">
      <h2 class="text-lg font-bold">留下你的名字與生日留言</h2>
      <input v-model="name" class="input" maxlength="40" placeholder="name（最多 40 字）" />
      <textarea v-model="msg" class="input min-h-28" maxlength="200" placeholder="birth msg（最多 200 字）" />
      <div class="grid grid-cols-2 gap-2 mt-auto">
        <button class="btn" @click="step = 'credits'">略過</button>
        <button class="btn-primary" :disabled="sending || !name || !msg" @click="submit">送出</button>
      </div>
    </template>

    <template v-else>
      <h2 class="text-lg font-bold">製作名單</h2>
      <ul class="text-sm text-white/70 flex flex-col gap-1">
        <li v-for="(c, i) in texts.credits" :key="i">{{ c }}</li>
      </ul>
      <p v-if="sent" class="text-xs text-white/40">留言已送出，待審核後公開。</p>
      <button class="btn-primary mt-auto" @click="goHome">回首頁</button>
    </template>
  </section>
</template>
