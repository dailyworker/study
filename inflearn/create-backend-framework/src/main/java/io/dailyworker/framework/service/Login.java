package io.dailyworker.framework.service;

import io.dailyworker.framework.aop.CustomRequest;
import io.dailyworker.framework.aop.Session;
import io.dailyworker.framework.controller.BizController;
import io.dailyworker.framework.controller.CustomRequestContext;
import io.dailyworker.framework.controller.MasterController;
import io.dailyworker.framework.db.Table;
import io.dailyworker.framework.security.CryptPin;

import java.sql.SQLException;

public class Login implements BizController {
    public String login() throws Exception {
        CustomRequest customRequest = CustomRequestContext.get();

        String id = customRequest.getString("ID");
        String pin = customRequest.getString("PIN");
        pin = CryptPin.cryptPin(pin, id);

        LoginDao loginDao = LoginDao.getLoginDao();

        Table user = loginDao.firstLoginPhase();

        if(user.size() != 1) {
            customRequest.put("result", "패스워드가 일치하지 않습니다.");
            return MasterController.execute("loginView");
        }

        CustomRequest maybeUser = user.getCustomRequest();

        if(!pin.equals(maybeUser.getString("pin"))) {
            customRequest.put("result", "패스워드가 일치하지 않습니다.");

            // 실패횟수가 증가되는 페이즈로 호출했을 때 1이 아니면 1을 초과하거나 혹은 증가 안될 수 있다.
            if(loginDao.secondLoginPhase() != 1) {
                throw new Exception("알 수없는 에러가 발생하였습니다.");
            }

            return MasterController.execute("loginView");
        }

        int failCnt = maybeUser.getInt("FAIL_CNT");
        if(failCnt > 5) {
            customRequest.put("result", "로그인을 5회 이상 시도하였습니다.");
            return MasterController.execute("loginView");
        }

        // 로그인 성공 처리
        if (loginDao.lastLoginPhase() != 1) {
            throw new Exception("알 수 없는 에러가 발생하였습니다.");
        }

        String auth = maybeUser.getString("AUTH");
        String ip = maybeUser.getString(CustomRequest.KEY_REMOTE_ADDR);

        Session session = new Session(id, auth, ip);
        customRequest.setSession(session);

        return MasterController.execute("empView");
    }

    public String logout() throws Exception {
        CustomRequestContext.get().setSession(null);
        return loginView();
    }

    private String loginView() {
        return "loginView";
    }
}
