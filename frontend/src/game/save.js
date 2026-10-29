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

// 偏好設定（難度等），與存檔分開，不隨新局清除
const PREFS_KEY = 'wot:prefs:v1'
const DEFAULT_PREFS = { difficulty: 'normal' }

export function loadPrefs() {
  try {
    const raw = localStorage.getItem(PREFS_KEY)
    return { ...DEFAULT_PREFS, ...(raw ? JSON.parse(raw) : {}) }
  } catch {
    return { ...DEFAULT_PREFS }
  }
}

export function writePrefs(patch) {
  try {
    localStorage.setItem(PREFS_KEY, JSON.stringify({ ...loadPrefs(), ...patch }))
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
