package io.dailyworker.framework.domain;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.controller.BizController;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.MasterController;

public class Login implements BizController {
    public String login() throws Exception {
        CustomRequest customRequest = CustomRequestContext.get();
        boolean isLogin = customRequest.getBoolean("isLogin");

        if(isLogin) {
            return MasterController.execute("select");
        }
        return loginView();
    }

    private String loginView() {
        return "LoginView";
    }
}
