package adapter;

import adapter.security.LoginHandler;
import adapter.security.UserDetailsService;

public class App {
    public static void main(String[] args) {
        AccountService accountService = new AccountService();
        LoginHandler loginHandler = new LoginHandler(accountService);
        String login = loginHandler.login("seansin", "seansin");
        System.out.println(login);
    }
}
