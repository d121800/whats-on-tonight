import { defineStore } from 'pinia'
import { CONFIG, clamp, randInt, fmtClock, fmtDate } from '../game/config'
import { loadSave, writeSave, clearSave } from '../game/save'

// ------------------------------------------------------------
// 遊戲狀態機
// scene 值即「目前所在狀態」，GameView 依此切換場景元件。
// 流程對照 documents/畫面流程與狀態機.md
// ------------------------------------------------------------
export const SCENES = [
  'tutorial', 'room', 'phone', 'streamSetup', 'streamLive', 'streamResult',
  'sleep', 'collapse', 'dayTransition', 'birthdayStream', 'ending',
]

const freshState = () => ({
  scene: 'tutorial',
  difficulty: 'normal',
  day: 1,
  clock: CONFIG.game.homeTime,      // 活動日分鐘數，可超過 1440（跨午夜仍屬同一活動日）
  stamina: CONFIG.stamina.max,
  stress: 0,                        // 僅直播中使用
  stats: { ...CONFIG.stats.initial },
  catCountToday: 0,
  tutorialDone: false,
  phoneUnread: true,                // 手機圖示紅點：每天到家時有新訊息 [暫定]
  collapsed: false,                 // 本輪是否曾累倒（供成就用）
  pendingSleepForced: false,
  currentStream: null,              // { title, type, friends, hours }
  qteActive: false,                 // 直播中 QTE 進行中（倒數暫停）
  qteCountThisStream: 0,
  qteResults: [],                   // 本場 QTE 結果，供結算顯示
  lastResult: null,                 // 結算資料
  transitionNote: '',               // dayTransition 顯示的文字
  log: [],
})

export const useGameStore = defineStore('game', {
  state: freshState,

  getters: {
    dateLabel: (s) => fmtDate(s.day),
    clockLabel: (s) => fmtClock(s.clock),
    isBirthday: (s) => s.day === CONFIG.game.totalDays,
    difficultyCfg: (s) => CONFIG.difficulty[s.difficulty],
    canPetCat: (s) => s.catCountToday < CONFIG.stamina.cat.dailyLimit,
    mood: (s) => {
      const t = CONFIG.stream.moodThresholds
      if (s.stress < t.calm) return 'calm'
      if (s.stress < t.excited) return 'happy'
      return 'excited'
    },
  },

  actions: {
    // ---------- 存檔 ----------
    persist() {
      writeSave(this.$state)
    },
    hasSave() {
      return !!loadSave()
    },
    resume() {
      const s = loadSave()
      if (s) this.$patch(s)
      return !!s
    },
    newGame(difficulty = 'normal') {
      clearSave()
      this.$patch(freshState())
      this.difficulty = difficulty
      this.persist()
    },
    reset() {
      clearSave()
      this.$patch(freshState())
    },

    // ---------- 共用 ----------
    goto(scene) {
      if (!SCENES.includes(scene)) throw new Error('unknown scene: ' + scene)
      this.scene = scene
      this.persist()
    },
    note(text) {
      this.log.push({ day: this.day, clock: this.clock, text })
      if (this.log.length > 200) this.log.shift()
    },
    changeStamina(delta) {
      const mul = delta < 0 ? this.difficultyCfg.staminaCostMul : 1
      this.stamina = clamp(this.stamina + Math.round(delta * mul), -999, CONFIG.stamina.max)
    },
    /** 推進時間並檢查累倒 / 強制睡覺。回傳 true 代表場景已被接管。 */
    advanceTime(minutes) {
      this.clock += minutes
      return this.checkFallthrough()
    },
    checkFallthrough() {
      if (this.stamina <= 0) {
        this.goto('collapse')
        return true
      }
      if (this.clock >= CONFIG.game.forceSleepTime) {
        this.pendingSleepForced = true
        this.goto('sleep')
        return true
      }
      this.persist()
      return false
    },

    // ---------- 教學 ----------
    /** 教學走完：聚焦螢幕後點螢幕 → 直接進開播準備（草圖 2 之後接開播準備畫面） */
    finishTutorial() {
      this.tutorialDone = true
      this.goto('streamSetup')
    },
    /** 教學 Skip：回房間自由行動 [暫定] */
    skipTutorial() {
      this.tutorialDone = true
      this.goto('room')
    },

    // ---------- 日常 ----------
    petCat() {
      if (!this.canPetCat) {
        this.catCountToday++
        this.note('吸貓被拒絕')
        this.persist()
        return { refused: true }
      }
      const c = CONFIG.stamina.cat
      const gain = randInt(c.min, c.max)
      this.catCountToday++
      this.changeStamina(gain)
      this.note('吸貓 +' + gain)
      this.advanceTime(c.minutes)
      return { refused: false, gain }
    },
    /** 滑手機：耗時 30 分鐘後進 phone；若推進時間後被強制睡覺／累倒則不進 */
    openPhone() {
      this.phoneUnread = false
      if (!this.advanceTime(CONFIG.phone.minutes)) this.goto('phone')
    },
    goOut() {
      const o = CONFIG.stamina.out
      const hours = randInt(o.hoursMin, o.hoursMax)
      // [暫定] 回復量依時數線性插值；手稿只說「受時間影響」
      const ratio = (hours - o.hoursMin) / Math.max(1, o.hoursMax - o.hoursMin)
      const gain = Math.round(o.min + (o.max - o.min) * ratio)
      this.changeStamina(gain)
      this.note('出門 ' + hours + ' 小時 +' + gain)
      this.advanceTime(hours * 60)
      return { hours, gain }
    },

    // ---------- 直播 ----------
    startStream({ title, type, friends, hours }) {
      this.currentStream = { title, type, friends, hours }
      this.stress = CONFIG.stream.stressInit
      this.qteActive = false
      this.qteCountThisStream = 0
      this.qteResults = []
      this.goto('streamLive')
    },
    /** 每秒由 StreamLiveScene 呼叫；回傳是否觸發 QTE */
    maybeTriggerQte(elapsedSec) {
      const q = CONFIG.qte
      if (this.qteActive) return false
      if (this.currentStream?.birthday) return false
      if (elapsedSec < q.firstSafeSeconds) return false
      if (this.qteCountThisStream >= q.maxPerStream) return false
      const byStress = this.stress >= q.triggerStress
      const byChance = Math.random() < q.randomChancePerSec
      if (!byStress && !byChance) return false
      this.qteActive = true
      this.qteCountThisStream++
      return true
    },
    resolveQte({ success, reason }) {
      const q = CONFIG.qte
      const raw = success ? q.successStress : q.failStress
      const delta = Math.round(raw * (raw > 0 ? this.difficultyCfg.stressMul : 1))
      this.stress = clamp(this.stress + delta, 0, CONFIG.stream.stressMax)
      this.qteResults.push({ success, reason, delta })
      this.qteActive = false
      this.note('QTE ' + (success ? '煞車成功' : '失控') + ' 壓力' + (delta >= 0 ? '+' : '') + delta)
    },
    liveCommand(key) {
      const cmd = CONFIG.stream.commands[key]
      if (!cmd) return
      const delta = Math.round(cmd.stress * (cmd.stress > 0 ? this.difficultyCfg.stressMul : 1))
      this.stress = clamp(this.stress + delta, 0, CONFIG.stream.stressMax)
    },
    finishLive() {
      const s = this.currentStream
      const staminaFromStress = -Math.round(this.stress * CONFIG.stream.stressToStamina)
      const staminaFromHours = CONFIG.stamina.streamPerHour * s.hours
      const gain = CONFIG.stats.perStream
      this.stats.subscribers += gain.subscribers
      this.stats.shorts += gain.shorts
      this.stats.popularity += gain.popularity
      this.lastResult = {
        ...s,
        stress: this.stress,
        mood: this.mood,
        staminaDelta: staminaFromStress + staminaFromHours,
        statsDelta: { ...gain },
        qte: [...this.qteResults],
      }
      this.qteActive = false
      this.changeStamina(staminaFromStress + staminaFromHours)
      this.note('直播「' + s.title + '」結束，壓力 ' + this.stress)
      this.goto('streamResult')
    },
    closeStreamResult() {
      const s = this.currentStream
      const hours = s?.hours ?? 1
      const wasBirthday = !!s?.birthday
      this.currentStream = null
      this.stress = 0
      if (wasBirthday) {
        this.goto('ending')
        return
      }
      if (!this.advanceTime(hours * 60)) this.goto('room')
    },

    // ---------- 睡覺 / 跳日 ----------
    sleep() {
      const t = CONFIG.sleepTable
      let pct = t[t.length - 1].pct
      for (const row of t) {
        if (this.clock <= row.at) { pct = row.pct; break }
      }
      const bedtime = fmtClock(this.clock)
      this.pendingSleepForced = false
      this.stamina = Math.round(CONFIG.stamina.max * pct / 100)
      this.transitionNote = bedtime + ' 就寢，體力恢復至 ' + pct + '%，上班扣 ' + Math.abs(CONFIG.stamina.work)
      this.nextDay(1, { work: true })
    },
    collapse() {
      // [手稿] 跳過隔天整天，翹班、睡過直播；醒來狀態 [暫定]
      this.collapsed = true
      this.stamina = Math.round(CONFIG.stamina.max * CONFIG.collapse.wakeStaminaPct / 100)
      this.transitionNote = '累倒跳日：翹班、睡過直播'
      this.nextDay(2, { work: false })
    },
    nextDay(delta, { work }) {
      const last = CONFIG.game.totalDays
      // [手稿] 生日直播必定觸發：跳日若會越過第七天，仍停在第七天。轉場 [待定]
      this.day = Math.min(last, this.day + delta)
      this.clock = CONFIG.game.homeTime
      this.catCountToday = 0
      this.phoneUnread = true
      if (work) this.changeStamina(CONFIG.stamina.work)
      if (this.isBirthday && CONFIG.stream.birthdayStaminaFill) this.stamina = CONFIG.stamina.max
      this.goto('dayTransition')
    },

    // ---------- 第七天 ----------
    startBirthdayStream() {
      this.currentStream = { title: '生日直播', type: 'chat', friends: [], hours: 1, birthday: true }
      this.stress = CONFIG.stream.stressInit
      this.qteActive = false
      this.qteCountThisStream = 0
      this.qteResults = []
      this.goto('birthdayStream')
    },
    finishGame() {
      this.reset()
    },
  },
})
