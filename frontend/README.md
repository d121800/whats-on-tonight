# frontend

Vue 3 + Vite + Tailwind CSS v4 的 SPA。開發規範見根目錄 `CLAUDE.md`。

## 指令

```bash
npm install     # 安裝依賴
npm run dev     # 開發伺服器
npm run build   # 建置到 dist/
npm run preview # 預覽建置結果
```

## 重要檔案

- `src/stores/game.js` 遊戲狀態機
- `src/game/config.js` 暫定數值
- `src/data/*.json` 文本與 placeholder 資料
- `src/api/client.js` 後端 API 封裝，`VITE_API_BASE` 留空走 mock

## 環境變數

複製 `.env.example` 為 `.env.local`，填 `VITE_API_BASE`。`.env.local` 不進 git。
