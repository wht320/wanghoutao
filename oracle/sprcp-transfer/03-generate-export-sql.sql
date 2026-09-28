-- 生成「每表最多 500 行」的查询。Oracle 11.2 没有 FETCH FIRST，必须用 ROWNUM。
-- 在 DBeaver 结果里复制 export_sql，对大表用「导出查询结果」，避免把整表拉到客户端。

SELECT
    table_name,
    'SELECT * FROM SPRCP.' || table_name || ' WHERE ROWNUM <= 500' AS export_sql
FROM all_tables
WHERE owner = 'SPRCP'
ORDER BY table_name;
