-- 内网 DBeaver 执行。列出 SPRCP 模式下全部表（Oracle 11.2）。
-- 若当前登录用户就是 SPRCP，也可改用 user_tables。

SELECT table_name
FROM all_tables
WHERE owner = 'SPRCP'
ORDER BY table_name;
