-- ============================================================
-- 《今晚播什麼？》資料庫 schema（Supabase / Postgres）
-- 規格：backend/API.md
-- 命名：表名 WOT_ + 大駝峰，欄位小駝峰，全部加雙引號保留大小寫。
-- 執行：Supabase 後台 → SQL Editor → 貼上整份執行。可重複執行（已存在則略過）。
-- ============================================================

-- ---------- 1. 遊戲開始時間（統計用） ----------
create table if not exists "WOT_StartTime" (
  "id"         bigserial   primary key,
  "createTime" timestamptz not null default now()
);
create index if not exists "WOT_StartTime_createTime_idx"
  on "WOT_StartTime" ("createTime");

-- ---------- 2. 直播留言（預設文本） ----------
create table if not exists "WOT_LiveTxt" (
  "id"     serial       primary key,
  "type"   varchar(20),                 -- 直播類型代碼（WOT_Setting livetype），null = 通用
  "who"    varchar(20),                 -- 朋友代碼（WOT_Setting who），null = 不限朋友
  "msg"    varchar(200) not null,
  "remark" varchar(200)                 -- 保留
);
create index if not exists "WOT_LiveTxt_type_idx" on "WOT_LiveTxt" ("type");
create index if not exists "WOT_LiveTxt_who_idx"  on "WOT_LiveTxt" ("who");

-- ---------- 3. 生日留言（玩家輸入） ----------
create table if not exists "WOT_BirthMsg" (
  "id"         serial       primary key,
  "name"       varchar(40)  not null check (btrim("name") <> ''),
  "msg"        varchar(200) not null check (btrim("msg")  <> ''),
  "isOk"       boolean      not null default false,   -- 人工審核通過才公開
  "createTime" timestamptz  not null default now()
);
-- 只索引已審核的資料：API 2 order=latest 用
create index if not exists "WOT_BirthMsg_ok_createTime_idx"
  on "WOT_BirthMsg" ("createTime" desc) where "isOk" = true;

-- ---------- 4. 代碼對照 ----------
create table if not exists "WOT_Setting" (
  "id"    serial       primary key,
  "type"  varchar(40)  not null,        -- 分類：livetype、who、api
  "name"  varchar(40)  not null,        -- 代碼
  "value" varchar(200),                 -- 說明／顯示文字
  constraint "WOT_Setting_type_name_uq" unique ("type", "name")
);

-- ---------- RLS ----------
-- 開啟 RLS 且不建 policy：Supabase 公開 API（anon key）無法讀寫。
-- 後端以資料庫連線字串（postgres 角色）連線，不受 RLS 影響。
alter table "WOT_StartTime" enable row level security;
alter table "WOT_LiveTxt"   enable row level security;
alter table "WOT_BirthMsg"  enable row level security;
alter table "WOT_Setting"   enable row level security;

-- ---------- 初始資料：代碼對照 ----------
-- 代碼沿用前端 frontend/src/data/stream-types.json、friends.json 的 id（方案 A）。
-- value 的中文名稱取自前端資料檔，朋友名稱前端標為 placeholder，定案後再更新。
insert into "WOT_Setting" ("type", "name", "value") values
  ('livetype', 'action',   '動作遊戲'),
  ('livetype', 'romance',  '戀愛遊戲'),
  ('livetype', 'horror',   '恐怖遊戲'),
  ('livetype', 'health',   '衛教'),
  ('livetype', 'chat',     '雜談'),
  ('livetype', 'multi',    '團康遊戲'),
  ('livetype', 'birthday', '生日'),
  ('who',      'dog',      'Matsuko'),
  ('who',      'cat',      '貓下去'),
  ('who',      'seal',     '豹子頭'),
  ('who',      'ghost',    '瓦哈哈'),
  ('who',      'chick',    '花雕雞'),
  -- API 可調數值：改這裡即可，後端最晚 5 分鐘（SETTING_CACHE_SECONDS）後生效，不用重新部署
  ('api',      'start.perMinute',       '5'),    -- /api/start 每 IP 每分鐘次數
  ('api',      'birthmsg.perMinute',    '3'),    -- /api/birthmsg 每 IP 每分鐘次數
  ('api',      'birthday.limitDefault', '100'),  -- 生日留言 limit 預設
  ('api',      'birthday.limitMax',     '500')   -- 生日留言 limit 上限
on conflict ("type", "name") do nothing;
