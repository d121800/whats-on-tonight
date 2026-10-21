import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// VITE_BASE：GitHub Pages 需設為 /<repo 名稱>/，Vercel 與本機用預設 /
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  base: process.env.VITE_BASE ?? '/',
})
