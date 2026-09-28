-- 每张表大约多少行（统计信息，可能不是精确值）。
-- 精确计数会锁表且很慢，内网大数据表不要用 COUNT(*) 扫全表。

SELECT table_name, num_rows, last_analyzed
FROM all_tables
WHERE owner = 'SPRCP'
ORDER BY table_name;
