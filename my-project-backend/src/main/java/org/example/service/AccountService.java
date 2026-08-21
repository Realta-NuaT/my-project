package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account> , UserDetailsService {
    Account findAccountByUsernameOrEmail(String text);
    Account findAccountById(int id);
    String registerEmailVerifyCode(String type, String email, String ip);
    String registerEmailAccount(EmailRegisterVO vo);
    String resetConfirm(ConfirmResetVO vo);
    String resetEmailAccountPassword(EmailResetVO vo);
    String modifyEmail(int id, ModifyEmailVO vo);
    String changePassword(int id, ChangePasswordVO vo);
    void modifyPassword(int id, String newPassword);
}
