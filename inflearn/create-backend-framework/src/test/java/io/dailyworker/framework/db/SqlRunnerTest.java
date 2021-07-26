package io.dailyworker.framework.db;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.TransactionContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SqlRunnerTest {
    @Test
    @DisplayName("DB 커넥트 테스트")
    public void connect_test() throws Exception {
        //given
        Transaction transaction = TransactionContext.get();
        //when
        transaction.generateConnection();
        //then
        assertNotEquals(transaction.hasNotConnection(), Boolean.TRUE);
    }
    @Test
    @DisplayName("조건을 추가하여 SELECT 쿼리를 제대로 수행할 수 있다.")
    public void select_test() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("EMP_NAME", "AAA");

        SqlRunner sql = SqlRunner.getSqlRunner();

        //when
        Table table = sql.getTable("SQL_SELECT_BY_USERNAME");
        String[] cols = table.getColumns();
        
        //then
        assertAll(
                () -> assertEquals(2, table.size()),
                () -> assertEquals("EMP_NO", cols[0]),
                () -> assertEquals("EMP_NAME", cols[1]),
                () -> assertEquals("HP_N", cols[2]),
                () -> assertEquals("N01", table.find("EMP_NO", 0)),
                () -> assertEquals("N03", table.find("EMP_NO", 1))
        );
    }
    @Test
    @DisplayName("UPDATE 쿼리가 정상적으로 수행된다.")
    public void update_test() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("EMP_NO", "N01");
        customRequest.put("HP_N", "010-2222-3333");

        SqlRunner sql = SqlRunner.getSqlRunner();

        //when
        int i = sql.executeSql("SQL_UPDATE_EMP");

        //then
        assertEquals(1, i);
        TransactionContext.commit();
    }
}