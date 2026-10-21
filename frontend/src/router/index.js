import { createRouter, createWebHashHistory } from 'vue-router'

// 先用 hash 模式配合 GitHub Pages；上 Vercel 後可改 createWebHistory。
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', name: 'title', component: () => import('../views/TitleView.vue') },
    { path: '/game', name: 'game', component: () => import('../views/GameView.vue') },
    { path: '/settings', name: 'settings', component: () => import('../views/SettingsView.vue') },
    { path: '/about', name: 'about', component: () => import('../views/AboutView.vue') },
    { path: '/achievements', name: 'achievements', component: () => import('../views/AchievementsView.vue') },
    { path: '/birthday', name: 'birthday', component: () => import('../views/BirthdayView.vue') },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

export default router
