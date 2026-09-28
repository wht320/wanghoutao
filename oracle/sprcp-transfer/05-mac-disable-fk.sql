-- 用 SPRCP 登录本地库后执行。导入数据前先关掉外键，避免每表只导 500 行时子表对不上父表。

BEGIN
    FOR c IN (
        SELECT constraint_name, table_name
        FROM user_constraints
        WHERE constraint_type = 'R'
          AND status = 'ENABLED'
    ) LOOP
        EXECUTE IMMEDIATE 'ALTER TABLE "' || c.table_name || '" DISABLE CONSTRAINT "' || c.constraint_name || '"';
    END LOOP;
END;
/
