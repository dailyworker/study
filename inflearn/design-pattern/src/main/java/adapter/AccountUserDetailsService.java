package adapter;

import adapter.security.UserDetails;
import adapter.security.UserDetailsService;

public class AccountUserDetailsService implements UserDetailsService {
    // Adaptee 서비스
    AccountService accountService;

    public AccountUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUser(String username) {
        Account account = accountService.findAccountByUsername(username);
        return new AccountUserDetails(account);
    }
}
