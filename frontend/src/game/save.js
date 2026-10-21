// localStorage 存檔。只存可序列化的 game store 狀態。
const KEY = 'wot:save:v1'

export function loadSave() {
  try {
    const raw = localStorage.getItem(KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function writeSave(state) {
  try {
    localStorage.setItem(KEY, JSON.stringify(state))
  } catch {
    /* 私密模式或容量不足時靜默失敗 */
  }
}

export function clearSave() {
  try {
    localStorage.removeItem(KEY)
  } catch {
    /* ignore */
  }
}

/** 下載紀錄：手稿有此想法，格式 [暫定] 為 JSON */
export function exportSave(state) {
  const blob = new Blob([JSON.stringify(state, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'whats-on-tonight-save.json'
  a.click()
  URL.revokeObjectURL(url)
}
