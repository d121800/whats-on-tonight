<script setup>
// 手機直式優先：外層限制最大寬度，桌機置中顯示。
</script>

<template>
  <div class="min-h-full flex justify-center">
    <div class="w-full max-w-md md:max-w-2xl lg:max-w-4xl min-h-full flex flex-col">
      <RouterView />
    </div>
  </div>
</template>
