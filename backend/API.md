# API 規格（草案 v0）

來源：手稿第 8、9 張。欄位名稱依手稿，型別為 Postgres 對應建議。
前端封裝在 `frontend/src/api/client.js`。所有回應為 JSON，UTF-8。

## 共通

- Base URL 由前端 `VITE_API_BASE` 設定，路徑皆以 `/api` 開頭。
- 錯誤回應：`{ "isSuccess": false, "error": "<訊息>" }`，HTTP 4xx / 5xx。
- CORS 需允許 GitHub Pages 與 Vercel 的前端來源。

## 1. 開始時間記入

`POST /api/start`

- Request body：無
- Response：`{ "isSuccess": true }`
- 寫入 `starttime.createtime = now()`

## 2. 取得直播留言

`GET /api/comments?type=<string>&who=<string>`

- `type`：直播分類 id（action、romance、horror、health、chat、multi）
- `who`：角色 id（dog、cat、seal、ghost、chick），可省略
- 手稿第三個 input 已劃掉，不納入
- Response：陣列

```json
[
  { "id": 1, "msg": "留言內容", "type": "horror", "remark": null }
]
```

- `type`、`remark` 可為 null（手稿註記「可 null」，指向範圍待確認）

## 3. 寫入生日留言

`POST /api/birthmsg`

```json
{ "name": "最多 40 字", "msg": "最多 200 字" }
```

- 寫入 `birthmsg`，`createtime = now()`，`isOk = false`
- Response：`{ "isSuccess": true }`
- isOk 是否代表人工審核後公開，手稿未明說，待確認

## 4. 首次提示文字（待辨識）

手稿「初次實…〈alert〉信…文字」，完整文案與觸發條件待辨識，暫不定義端點。

## 資料表草案（依手稿，名稱暫不處理）

| 表 | 欄位 | 備註 |
|---|---|---|
| starttime | createtime timestamptz | 遊戲開始時間 |
| birthmsg | id serial、name varchar(40)、msg varchar(200)、isOk boolean default false、createtime timestamptz | |
| txt | id serial、type int、msg varchar(200) | type 有 100、200 兩類，加 index；長度待確認 |
| setting | type、name、value | 手稿註記 exceptType／Live tree／800／預設數，字義待辨識 |

手稿原為 nvarchar，Postgres 改用 varchar / text。
