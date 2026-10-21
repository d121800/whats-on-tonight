# backend

Java 後端，由後端負責人維護。此資料夾目前只有規格，尚無程式碼。

## 已定案

- 語言：Java（框架由後端負責人決定；Render 免費方案只有 512 MB，建議 Java 21 並限制 heap，或選較輕的框架）
- 資料庫：Supabase（Postgres），以 JDBC 直連
- 測試期部署：Render，Root Directory 設為 `backend`
- API 規格：`API.md`，前端只呼叫這份規格

## 環境變數（不進 git）

| 名稱 | 用途 |
|---|---|
| `DATABASE_URL` | Supabase Postgres 連線字串 |
| `CORS_ORIGINS` | 允許的前端來源，測試期為 GitHub Pages 網址 |

## 給後端負責人

1. 在本資料夾建 Java 專案，專案檔（pom.xml 或 build.gradle）放在 `backend/` 根。
2. 依 `API.md` 實作三支 API，回傳 JSON。
3. Render 建 Web Service 時 Root Directory 填 `backend`。
4. 完成後更新 `documents/session/主控.md` 追蹤表計劃 02，並寫工作紀錄。
