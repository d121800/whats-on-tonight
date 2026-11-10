# backend

《今晚播什麼？》後端：Java 21 + Spring Boot，資料庫 Supabase（Postgres）。
API 規格：`API.md`（前端只呼叫這份規格）。

## 目錄

```
backend/
  API.md                 API 規格
  db/schema.sql          建表、索引、RLS、WOT_Setting 初始資料（可重複執行）
  db/seed-test.sql       測試假資料
  postman/               Postman 測試集合
  src/main/java/...      程式
    start/               API 1  POST /api/start
    comments/            API 2  GET  /api/comments
    birthmsg/            API 3  POST /api/birthmsg（含檢核 BirthMsgValidator）
    setting/             WOT_Setting 讀取與快取、可調數值 ApiLimits
    common/              錯誤格式、頻率限制、取 IP
    config/              CORS、body 大小限制
  src/main/resources/application.yml
  Dockerfile             Render 部署用
  pom.xml
```

## 環境變數（不進 git）

| 名稱 | 必填 | 用途 | 例 |
|---|---|---|---|
| `DB_URL` | ✔ | JDBC 連線字串 | `jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require` |
| `DB_USER` | ✔ | 資料庫帳號 | `postgres.<project-ref>` |
| `DB_PASSWORD` | ✔ | 資料庫密碼 | |
| `CORS_ORIGINS` | | 允許的前端來源，逗號分隔；預設 `http://localhost:5173` | `https://d121800.github.io,http://localhost:5173` |
| `PROXY_HOPS` | | 前面有幾層 proxy；預設 0 | 本機 0，Render 1 |
| `SETTING_CACHE_SECONDS` | | WOT_Setting 快取秒數；預設 300 | 本機測試可設 5 |
| `PORT` | | 服務埠；預設 8080，Render 會自動給 | |

**Supabase 連線字串**：後台 → Project Settings → Database → Connection string，選 **Session pooler**
（直連位址可能只支援 IPv6，Render 連不上）。Supabase 給的是 `postgresql://user:pass@host:5432/postgres` 格式，
要拆成上面三個變數，並在 host 前面加 `jdbc:postgresql://`、後面加 `?sslmode=require`。

## 建資料庫

Supabase 後台 → SQL Editor：

1. 貼上 `db/schema.sql` 執行。
2. 測試環境再執行 `db/seed-test.sql`（假資料都標「（測試）」，檔頭有清除指令）。

可調數值（頻率限制、生日留言筆數）在 `WOT_Setting` 的 `type='api'`，改了不用重新部署，最晚 5 分鐘生效。

## 本機啟動

需要 JDK 21 與 Maven（或用 IntelliJ IDEA 直接開 `backend/` 資料夾）。

```bash
cd backend
export DB_URL='jdbc:postgresql://...'
export DB_USER='postgres.xxxx'
export DB_PASSWORD='...'
mvn spring-boot:run
```

IntelliJ：Run Configuration 的 Environment variables 填上面三個變數，執行 `WotApplication`。

啟動後 API 在 `http://localhost:8080`，用 Postman 匯入 `postman/wot-api.postman_collection.json` 測試
（`baseUrl` 變數預設就是 localhost:8080；執行方式見 collection 說明）。

單元測試：`mvn test`（目前只有生日留言檢核，不需要資料庫）。

## 部署（Render）

GitHub 本身不會執行後端程式（GitHub Pages 只能放靜態網頁），後端要放在 Render：
Render 連結 GitHub repo，**之後 push 到 main 就會自動重新部署**。

第一次設定：

1. Render → New → Web Service → 選 GitHub repo `d121800/whats-on-tonight`。
2. Root Directory 填 `backend`；Runtime 選 **Docker**（Render 沒有原生 Java，會用 `backend/Dockerfile` 建置）。
3. Instance Type 選 Free。
4. Environment 填 `DB_URL`、`DB_USER`、`DB_PASSWORD`、`CORS_ORIGINS`，以及 `PROXY_HOPS=1`。
5. 部署完成後網址形如 `https://<服務名稱>.onrender.com`，交給前端填 `VITE_API_BASE`。

部署後確認：

- 用 Postman 把 `baseUrl` 改成 Render 網址再跑一次。
- `PROXY_HOPS` 是否正確：帶假的 `X-Forwarded-For: 1.2.3.4` 連打 `/api/birthmsg`，
  應該照樣被限流（429）；若沒被限流，表示取到的是偽造的值，要調整 `PROXY_HOPS`。

**免費方案限制**

- Render：15 分鐘沒有請求會休眠，下一個請求要等服務重新啟動（Spring Boot 約數十秒）。
- Supabase：專案一段時間沒有活動會被暫停，需到後台手動恢復。
