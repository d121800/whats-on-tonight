# 今晚播什麼？

七天直播生活模擬網頁遊戲。需求來源見 `documents/manuscript/`，開發規範見 `CLAUDE.md`。

## 結構

- `frontend/` Vue 3 + Vite + Tailwind CSS SPA
- `backend/` Java 後端（規格 `backend/API.md`）
- `documents/` 手稿、工作紀錄、派工、流程與數值文件

## 前端本機啟動

```bash
cd frontend
npm install
npm run dev
```

要接真後端時複製 `frontend/.env.example` 為 `frontend/.env.local` 並填 `VITE_API_BASE`。留空走 mock。

## 部署

- 測試：push 到 main 後 GitHub Actions 自動部署 `frontend/` 到 GitHub Pages。
  首次需到 repo Settings → Pages 把 Source 設為 GitHub Actions。
- 正式：Vercel，Root Directory 填 `frontend`。
- 後端：Render，Root Directory 填 `backend`。
