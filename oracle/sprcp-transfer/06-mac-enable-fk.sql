-- 数据导入完成后可选执行。若报 ORA-02298（父键不存在），本地开发可保持外键禁用。

BEGIN
    FOR c IN (
        SELECT constraint_name, table_name
        FROM user_constraints
        WHERE constraint_type = 'R'
          AND status = 'DISABLED'
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'ALTER TABLE "' || c.table_name || '" ENABLE CONSTRAINT "' || c.constraint_name || '"';
        EXCEPTION
            WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE(c.table_name || '.' || c.constraint_name || ': ' || SQLERRM);
        END;
    END LOOP;
END;
/
