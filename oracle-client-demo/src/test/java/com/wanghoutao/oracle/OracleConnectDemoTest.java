package com.wanghoutao.oracle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OracleConnectDemoTest {

    @Test
    void fallsBackToLocalFreeDefaults() {
        assertEquals("jdbc:oracle:thin:@localhost:1521/FREEPDB1", OracleConnectDemo.DEFAULT_URL);
        assertEquals("system", OracleConnectDemo.DEFAULT_USER);
    }
}
