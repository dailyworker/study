package io.dailyworker.framework.domain;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.CustomRequestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginTest {
    @Test
    @DisplayName("로그인이 성공한다.")
    public void login_success_test() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("isLogin", true);

        //when
        Login login = new Login();
        login.login();

        //then
    }

    @Test
    @DisplayName("로그인이 실패한다.")
    public void login_fail_test() throws Exception {
        //given
        CustomRequest customRequest = CustomRequestContext.get();
        customRequest.put("isLogin", false);

        //when
        Login login = new Login();
        login.login();

        //then
    }
}
