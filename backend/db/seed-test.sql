-- ============================================================
-- 測試用假資料（Postman 驗證 API 用）
-- 只在測試環境執行；正式文本由文本人員提供後另行匯入。
-- 清除：delete from "WOT_LiveTxt" where "msg" like '（測試）%';
--       delete from "WOT_BirthMsg" where "msg" like '（測試）%';
-- ============================================================

insert into "WOT_LiveTxt" ("type", "who", "msg") values
  (null,       null,    '（測試）通用：阿萬晚安～'),
  (null,       null,    '（測試）通用：今天玩什麼？'),
  ('horror',   null,    '（測試）恐怖：好恐怖喔喔喔'),
  ('health',   null,    '（測試）衛教：這個衛教好實用'),
  (null,       'cat',   '（測試）不限類型＋貓：貓貓出現了！'),
  ('horror',   'ghost', '（測試）恐怖＋幽靈：鬼跟鬼一起玩恐怖遊戲'),
  ('birthday', null,    '（測試）生日：生日快樂！'),
  ('birthday', null,    '（測試）生日：蛋糕看起來好好吃');

-- isOk = true 兩筆、false 一筆：驗證未審核的不會被回傳
insert into "WOT_BirthMsg" ("name", "msg", "isOk") values
  ('測試甲', '（測試）已審核：阿萬生日快樂', true),
  ('測試乙', '（測試）已審核：一起慶生！',   true),
  ('測試丙', '（測試）未審核：不應出現',     false);
