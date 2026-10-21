// ============================================================
// 暫定數值設定檔
// 所有數字皆為「暫定」，來源標 [手稿] 者取自手稿整理檔，標 [暫定] 者為開發期占位值。
// 調整平衡只改這裡，不要把數字寫死在場景元件中。
// 時間以「活動日 00:00 起算的分鐘數」表示，可超過 1440（跨午夜仍屬同一活動日）。
// ============================================================

const H = 60 // 一小時的分鐘數

export const CONFIG = {
  game: {
    totalDays: 7,                 // [手稿] 七天
    startDate: '2026-03-22',      // [手稿] 3/22～3/28；年份 [暫定]
    homeTime: 19 * H,             // [手稿] 19:00 到家
    forceSleepTime: 27 * H,       // [手稿] 凌晨 03:00 要求睡覺；是否可拒絕 [待定]
  },

  stamina: {
    max: 100,                     // [手稿]
    work: -20,                    // [手稿] 上班固定扣 20
    cat: { min: 10, max: 30, minutes: 30, dailyLimit: 2 }, // [手稿]
    out: { min: 25, max: 40, hoursMin: 1, hoursMax: 3 },   // [手稿] 回復量受時數影響，對應關係 [待定]
    streamPerHour: -5,            // [暫定] 直播本身每小時耗體力；手稿未列
  },

  // [手稿] 入睡時間決定隔天體力；超過 03:00 視同 03:00 [暫定]
  sleepTable: [
    { at: 24 * H, pct: 100 },
    { at: 25 * H, pct: 90 },
    { at: 26 * H, pct: 75 },
    { at: 27 * H, pct: 60 },
  ],

  collapse: {
    // [手稿] 體力 ≤ 0 觸發累倒跳日，跳過隔天整天。醒來時間與體力 [暫定]
    wakeStaminaPct: 60,
  },

  stream: {
    hoursMin: 1, hoursMax: 4,     // [手稿]
    liveSeconds: 60,              // [手稿] 實際操作一分鐘
    stressInit: 0,                // [暫定] 每場初始壓力
    stressMax: 100,               // [暫定]
    commands: {                   // [手稿] 控場 +壓、炒熱 ++壓、衛教 -壓；量 [暫定]
      control: { stress: +5,  label: '控場' },
      hype:    { stress: +10, label: '炒熱' },
      health:  { stress: -8,  label: '衛教' },
    },
    stressToStamina: 0.2,         // [暫定] 結算扣體力 = 壓力 × 此係數
    moodThresholds: { calm: 30, excited: 70 }, // [暫定] 壓力 <30 平淡、<70 開心、否則激動
    birthdayStaminaFill: true,    // [手稿] 生日直播當天體力補滿
  },

  // [暫定] 成長數值：手稿結算畫面有 short、訂閱人等字樣，正式定義待補
  stats: {
    initial: { subscribers: 0, shorts: 0, popularity: 0 },
    perStream: { subscribers: 1, shorts: 2, popularity: 5 }, // [暫定] 每場基礎成長
  },

  // [手稿] 普通、瓦哈（極困難）；差異 [暫定]
  difficulty: {
    normal: { label: '普通', staminaCostMul: 1.0, stressMul: 1.0 },
    waha:   { label: '瓦哈', staminaCostMul: 1.5, stressMul: 1.5 },
  },
}

export function clamp(v, min, max) {
  return Math.min(max, Math.max(min, v))
}

export function randInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

/** 分鐘數 → "HH:MM"，超過 24:00 以隔日時刻顯示 */
export function fmtClock(minutes) {
  const m = ((minutes % 1440) + 1440) % 1440
  const h = Math.floor(m / 60)
  const mm = m % 60
  return `${String(h).padStart(2, '0')}:${String(mm).padStart(2, '0')}`
}

/** 第 day 天對應的日曆日期字串（M/D） */
export function fmtDate(day) {
  const d = new Date(CONFIG.game.startDate + 'T00:00:00')
  d.setDate(d.getDate() + (day - 1))
  return `${d.getMonth() + 1}/${d.getDate()}`
}
