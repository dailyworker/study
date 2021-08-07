package io.dailyworker.framework.aop;

import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.MasterController;
import io.dailyworker.framework.controller.TransactionContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @BeforeEach
    public void setUp() {
        Session session = new Session("id_D0", "D0", "127.0.0.1");
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.setSession(session);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        TransactionContext.commit();
    }

    @Test
    @DisplayName("유저 정보 조회 테스트")
    public void selectWithSession() throws Exception {
        MasterController.execute("select");
    }

    @Test
    @DisplayName("업데이트 접근 권한 테스트")
    public void updateWithSession() throws Exception {
        MasterController.execute("update");
    }
}