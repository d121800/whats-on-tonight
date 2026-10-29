<script setup>
// 房間舞台（草圖 2）：桌面、螢幕（點了開播）、主機、杯子，底部圖示列。
// 純 DOM + CSS 占位，美術定稿後換成圖片與熱區。
// 教學用 props：interactive=false 關掉所有點擊；spotlight 暗幕聚焦螢幕（只有螢幕可點）。
const props = defineProps({
  interactive: { type: Boolean, default: true },
  spotlight: { type: Boolean, default: false },
  showFairy: { type: Boolean, default: false },
  birthday: { type: Boolean, default: false },
  phoneBadge: { type: Boolean, default: false },
})
const emit = defineEmits(['monitor', 'chat', 'cat', 'phone', 'sleep', 'gear'])

function hit(name) {
  if (name === 'monitor') {
    if (props.interactive || props.spotlight) emit('monitor')
    return
  }
  if (props.interactive) emit(name)
}
</script>

<template>
  <div class="flex-1 flex flex-col">
    <!-- 房間本體 -->
    <div class="relative flex-1 min-h-64 md:min-h-80 flex flex-col justify-end overflow-hidden">
      <!-- placeholder：房間背景圖待美術 -->
      <div class="absolute inset-0 bg-gradient-to-b from-white/5 to-transparent" />

      <!-- 桌面 -->
      <div class="relative mx-4 h-32 md:h-44 rounded-t-2xl bg-white/10 border-t-4 border-white/20">
        <!-- 螢幕：點了開播 -->
        <button
          class="absolute left-1/2 -translate-x-1/2 -top-24 md:-top-32 w-44 h-28 md:w-60 md:h-36 rounded-lg bg-black border-4 border-white/30 flex items-center justify-center text-5xl transition"
          :class="spotlight ? 'z-30 ring-4 ring-accent shadow-[0_0_40px_rgba(242,140,40,.6)] animate-pulse' : 'hover:border-brand active:scale-95'"
          :disabled="!interactive && !spotlight"
          aria-label="電腦螢幕：開播"
          @click="hit('monitor')"
        >
          <span v-if="birthday">🎂</span>
          <span v-else class="text-white/80">▷</span>
        </button>
        <!-- 螢幕底座 -->
        <div class="absolute left-1/2 -translate-x-1/2 -top-2 w-16 h-3 rounded-full bg-white/20" />
        <!-- 主機 -->
        <div class="absolute right-6 -top-20 md:-top-28 w-10 md:w-12 h-24 md:h-32 rounded bg-white/15 flex flex-col items-center pt-2 gap-1">
          <div class="w-4 h-1 bg-white/40 rounded" /><div class="w-4 h-1 bg-white/40 rounded" />
        </div>
        <!-- 杯子 -->
        <div class="absolute right-20 md:right-24 -top-6 text-2xl md:text-3xl">☕</div>
        <!-- 阿邁小精靈（教學時出現在桌邊） -->
        <div v-if="showFairy" class="absolute left-3 -top-10 text-4xl md:text-5xl z-30 animate-bounce">🧚</div>
      </div>

      <!-- 教學暗幕：聚焦螢幕 -->
      <div v-if="spotlight" class="absolute inset-0 bg-black/75 z-20 pointer-events-none" />
    </div>

    <!-- 底部圖示列（草圖 2：對話泡泡、貓、手機、齒輪；睡覺入口 [暫定] 先放這裡） -->
    <nav class="grid grid-cols-5 gap-1 px-2 py-2 border-t border-white/10 bg-white/5 text-2xl">
      <button class="btn flex-col gap-0 py-1" :disabled="!interactive" title="對話（功能待定）" @click="hit('chat')">
        💬<span class="text-[10px] text-white/50">待定</span>
      </button>
      <button class="btn flex-col gap-0 py-1" :disabled="!interactive" @click="hit('cat')">
        🐱<span class="text-[10px] text-white/50">吸貓</span>
      </button>
      <button class="btn flex-col gap-0 py-1 relative" :disabled="!interactive" @click="hit('phone')">
        📱<span class="text-[10px] text-white/50">手機</span>
        <span v-if="phoneBadge" class="absolute top-1 right-3 w-2.5 h-2.5 rounded-full bg-red-500" />
      </button>
      <button class="btn flex-col gap-0 py-1" :disabled="!interactive" @click="hit('sleep')">
        🛏️<span class="text-[10px] text-white/50">睡覺</span>
      </button>
      <button class="btn flex-col gap-0 py-1" :disabled="!interactive" @click="hit('gear')">
        ⚙️<span class="text-[10px] text-white/50">設定</span>
      </button>
    </nav>
  </div>
</template>
