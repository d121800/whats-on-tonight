// 後端 API 封裝。VITE_API_BASE 留空時走 mock，方便前端獨立開發。
// 規格見 backend/API.md。

const BASE = import.meta.env.VITE_API_BASE || ''
const useMock = !BASE

async function request(path, options = {}) {
  const res = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) throw new Error(`API ${path} failed: ${res.status}`)
  return res.json()
}

export const api = {
  /** 1. 開始時間記入 */
  async recordStart() {
    if (useMock) return { isSuccess: true }
    return request('/api/start', { method: 'POST' })
  },

  /** 2. 取得直播留言：type 分類、who 角色 */
  async getLiveComments({ type, who }) {
    if (useMock) {
      const { default: mock } = await import('../data/mock-comments.json')
      return mock.filter((c) => (!c.type || c.type === type) && (!c.who || c.who === who))
    }
    const q = new URLSearchParams({ type, who }).toString()
    return request(`/api/comments?${q}`)
  },

  /** 3. 寫入生日留言 */
  async postBirthMsg({ name, msg }) {
    if (useMock) return { isSuccess: true }
    return request('/api/birthmsg', { method: 'POST', body: JSON.stringify({ name, msg }) })
  },
}
