# API 規格 v1

來源：手稿第 8、9 張；2026-11-10 joan 定案（見 `documents/worklog/20261110_1017_joan_工作紀錄.md`）。
資料表定義見 `backend/db/schema.sql`。前端封裝在 `frontend/src/api/client.js`。

| # | 方法 | 路徑 | 用途 |
|---|---|---|---|
| 1 | POST | `/api/start` | 記錄遊戲開始時間（統計遊玩人數與時間點） |
| 2 | GET | `/api/comments` | 取得直播留言；`type=birthday` 時改取生日留言 |
| 3 | POST | `/api/birthmsg` | 寫入生日留言 |

---

## 共通

- Base URL 由前端 `VITE_API_BASE` 設定，路徑皆以 `/api` 開頭。全程 HTTPS。
- Request / Response 皆為 JSON，UTF-8。
- 錯誤回應一律：

  ```json
  { "isSuccess": false, "error": "<訊息>" }
  ```

  | HTTP | 情況 |
  |---|---|
  | 400 | 參數或欄位不合規則 |
  | 429 | 同一 IP 呼叫過於頻繁 |
  | 500 | 伺服器或資料庫錯誤（訊息不帶內部細節） |

- **CORS**：只允許環境變數 `CORS_ORIGINS` 列出的來源（逗號分隔），例：
  `https://d121800.github.io,http://localhost:5173`。正式上線再加 Vercel 網址。
  只寫 scheme + 網域（+ port），不含路徑。
- **頻率限制**：以呼叫者 IP 計算，超過回 `429`。次數設定在 `WOT_Setting`（`type='api'`），
  改 DB 後最晚 5 分鐘生效，不用重新部署；沒有該筆設定時用程式預設值。
  IP 取法由環境變數 `PROXY_HOPS` 決定（本機 0 = 連線 IP；Render 1 = `X-Forwarded-For` 最後一個值）。
  不取第一個值，因為呼叫者可以自己偽造。
- SQL 一律參數綁定，不做字串拼接。

---

## 1. 開始時間記入

`POST /api/start`

- 用途：統計遊玩人數與時間點，遊戲中不會讀取。
- Request body：無
- 動作：`WOT_StartTime` 新增一筆，`createTime = now()`。
- 頻率限制：同一 IP 每分鐘 5 次（`WOT_Setting` api / `start.perMinute`）。
- Response `200`：

  ```json
  { "isSuccess": true }
  ```

---

## 2. 取得直播留言

`GET /api/comments?type=<string>&who=<string>&limit=<int>&order=<string>`

### 參數

| 參數 | 必填 | 說明 |
|---|---|---|
| `type` | 否 | 直播類型代碼，有效值為 `WOT_Setting` 中 `type='livetype'` 的 `name`（action、romance、horror、health、chat、multi、birthday） |
| `who` | 否 | 朋友代碼，有效值為 `WOT_Setting` 中 `type='who'` 的 `name`（dog、cat、seal、ghost、chick） |
| `limit` | 否 | **僅 `type=birthday` 有效**。回傳筆數上限，預設 100，最大 500（超過以 500 計）；兩個數字在 `WOT_Setting` api / `birthday.limitDefault`、`birthday.limitMax` |
| `order` | 否 | **僅 `type=birthday` 有效**。`random`（預設）或 `latest` |

### 參數的寬鬆處理

- `who` 未帶、空字串、字串 `"null"`、或不在有效值內 → 視為**不限朋友**。
- `type` 未帶、空字串、字串 `"null"`、或不在有效值內 → 視為**通用**。
- `limit` 不是正整數、`order` 不是 `random` / `latest` → `400`。

### 一般直播（type ≠ birthday）

從 `WOT_LiveTxt` 回傳**全部**符合條件的留言，不限筆數、不排序保證：

```sql
WHERE ("type" IS NULL OR "type" = :type)
  AND ("who"  IS NULL OR "who"  = :who)
```

- type 視為通用時，條件變成 `"type" IS NULL`。
- who 視為不限朋友時，條件變成 `"who" IS NULL`。

### 生日（type = birthday）

1. 從 `WOT_BirthMsg` 取 `"isOk" = true` 的留言，最多 `limit` 筆。
   - `order=random`：隨機取。
   - `order=latest`：依 `createTime` 由新到舊取。
2. 不足 `limit` 筆時，從 `WOT_LiveTxt` 隨機取 `"type" = 'birthday'` 或 `"type" IS NULL` 的留言補足（`who` 忽略）。
3. 回傳順序：生日留言在前（依步驟 1 的順序），補位留言在後。

### Response `200`

陣列，兩種來源格式一致，沒有值的欄位為 `null`：

```json
[
  { "id": 12, "msg": "生日快樂！", "type": "birthday", "who": null, "remark": null, "name": "小明", "source": "birthMsg" },
  { "id": 3,  "msg": "好恐怖喔喔喔", "type": "horror",   "who": null, "remark": null, "name": null,   "source": "liveTxt" }
]
```

| 欄位 | 說明 |
|---|---|
| `id` | 該筆在來源表的 id。**兩張表各自編號，可能重複**，需搭配 `source` 辨識 |
| `msg` | 留言內容 |
| `type` | liveTxt：該筆的 type（可 null）；birthMsg：固定 `"birthday"` |
| `who` | liveTxt：該筆的 who（可 null）；birthMsg：固定 `null` |
| `remark` | liveTxt：保留欄位（可 null）；birthMsg：固定 `null` |
| `name` | birthMsg：留言者名稱；liveTxt：固定 `null` |
| `source` | `"liveTxt"` 或 `"birthMsg"` |

查無資料回空陣列 `[]`。

> 生日留言是玩家輸入的內容，前台顯示時請以純文字呈現，不要當 HTML 渲染。

---

## 3. 寫入生日留言

`POST /api/birthmsg`

### Request body

```json
{ "name": "最多 40 字", "msg": "最多 200 字" }
```

### 檢核（依序執行，任一失敗回 `400`）

1. body 不是 JSON、或 `name` / `msg` 不是字串 → `400`。
2. 清理：移除控制字元（換行 `\n` 於 msg 保留，`\r\n` 轉為 `\n`）、零寬字元（U+200B～U+200F、U+2060、U+FEFF）、文字方向控制字元（U+202A～U+202E、U+2066～U+2069），再去除前後空白。
3. 清理後為空字串 → `400`「name 不可空白」／「msg 不可空白」。
4. 字數以 Unicode 字元數（code point）計算：`name` > 40 或 `msg` > 200 → `400`。
5. 含網頁語法 → `400`「內容含不允許的字元」。判定規則：
   - HTML 標籤：`<` 後接英文字母、`/`、`!` 或 `?`
   - `javascript:`、`vbscript:`、`data:text/html`（不分大小寫）
   - 事件屬性：`on` + 英文字母 + `=`（例：`onerror=`，不分大小寫）
6. request body 超過 4 KB → `400`「內容太長」。（實際在解析 JSON 前檢查，順序上最先發生）

不以黑名單排除 SQL 關鍵字（如 select、drop、`'`）；SQL injection 由參數綁定處理，留言照原文存入。

### 動作

- 寫入**清理後**的內容到 `WOT_BirthMsg`，`isOk = false`，`createTime = now()`。
- 審核：人工於 Supabase 後台將 `isOk` 改為 `true` 後，才會出現在 API 2 的生日留言中。
- 頻率限制：同一 IP 每分鐘 3 次（`WOT_Setting` api / `birthmsg.perMinute`），超過回 `429`。檢核失敗的請求也計入次數。

### Response `200`

```json
{ "isSuccess": true }
```

---

## 未納入

- 手稿「初次實…〈alert〉信…文字」（首次提示），完整文案與觸發條件待辨識，暫不定義端點。
- 生日留言分頁查詢（`before`）：前台一次取回後自行呈現，不需要。
