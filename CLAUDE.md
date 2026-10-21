# 《今晚播什麼？》專案規範

網頁模擬遊戲：扮演阿萬，七天內兼顧上班、體力與直播，第七天迎接生日直播。
手稿整理檔是需求的 source of truth：`documents/manuscript/今晚播什麼_手稿文字整理.md`。

## 開工前必做（每次 session 開始）

1. **先問對方的工作紀錄名字。** 開啟專案的人要用什麼名字寫工作紀錄？
   hannah 的名字固定是 `hannah`。沒問到名字之前不要寫工作紀錄。
2. **提醒讀最新一份工作紀錄。** 列出 `documents/worklog/` 裡最新的一份，
   提醒對方「要不要先看一下最新進度？」。對方說不用也可以，但一定要提醒。
3. 讀本檔與 `documents/畫面流程與狀態機.md`。

## 工作紀錄規範（必守）

- **每次工作都要有工作紀錄**，不論改動大小。
- 位置：`documents/worklog/`
- 檔名：`YYYYMMDD_HHMM_<名稱>_工作紀錄.md`，例如 `20261021_1611_hannah_工作紀錄.md`。
  時間取 session 開始時間。
- 內容依 `documents/worklog/_範本.md`：做了什麼、目前進度、下一步、待確認事項。
- session 結束前更新「目前進度」與「下一步」，讓下一個人接得上。
- 工作紀錄跟當次的 code 一起 commit。

## 派工規範

派工給 sub-agent 或其他 session 依個人全域 CLAUDE.md 的「派工」段與 `dispatch-sub-agent` skill 辦理。

- 暫存檔目錄：`documents/session/`
- 編號池與驗收追蹤表：`documents/session/主控.md`
- 單純查詢 / grep 不需走派工流程。

## 目錄結構

```
frontend/    Vue 3 + Vite + Tailwind CSS v4 SPA（前端）
backend/     Java 後端（由後端負責人維護），API 規格見 backend/API.md
documents/
  manuscript/   手稿整理檔與 17 張草圖（需求來源，不要改寫原文）
  worklog/      工作紀錄
  session/      派工暫存檔與主控表
  畫面流程與狀態機.md
  暫定數值與待定事項.md
.github/workflows/deploy-pages.yml   前端自動部署到 GitHub Pages
```

## 技術決定（已定案）

| 項目 | 決定 |
|---|---|
| 前端 | Vue 3、Vite、Tailwind CSS v4、vue-router（hash 模式）、Pinia |
| RWD | 手機直式優先，桌機以 `md:` 以上斷點擴展 |
| 遊戲引擎 | 不用，純 DOM + CSS |
| 存檔 | localStorage，見 `frontend/src/game/save.js` |
| 後端 | Java，資料庫 Supabase（Postgres），測試期部署 Render 免費方案 |
| 前端部署 | 測試期 GitHub Pages，正式 Vercel |
| 套件管理 | npm，Node 24 |

## 前端開發規範

- **數值一律放 `frontend/src/game/config.js`**，不要寫死在元件裡。
  每個數字標來源：`[手稿]` 取自手稿、`[暫定]` 開發占位、`[待定]` 規則未定。
- **文本與素材走資料檔**：`frontend/src/data/*.json`。placeholder 內容要標 `（placeholder）`。
- **遊戲流程只在 Pinia store 改狀態**：`frontend/src/stores/game.js`。
  場景元件只呼叫 action，不自己算數值。新增場景要同時更新 `SCENES` 陣列、
  `GameView.vue` 的 sceneMap、以及 `documents/畫面流程與狀態機.md`。
- 前端只呼叫 `backend/API.md` 定義的 REST 介面，不直接連 Supabase。
  `VITE_API_BASE` 留空時走 mock（`frontend/src/api/client.js`）。
- 手稿標「待確認」「待辨識」的內容不要自行定案，先做 placeholder 並在工作紀錄的「待確認」列出。

## git 規範

- 前端只動 `frontend/`，後端只動 `backend/`，共用文件在 `documents/`。
- 不 `git add -A` / `git add .`，一律明確 pathspec。
- 金鑰、連線字串、`.env.local` 不進 git。`frontend/.env.example` 只放空白範例。
- commit message 用 `type(scope): 說明`，scope 例：`frontend`、`backend`、`docs`、`session`。

## 不確定時

依個人全域 CLAUDE.md：名稱找不到就問、需求有多種解讀就列選項、要動沒指名的檔先說明。
