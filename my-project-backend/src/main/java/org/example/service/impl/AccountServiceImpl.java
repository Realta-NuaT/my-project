package org.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.*;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper,Account> implements AccountService {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FlowUtils flowUtils;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByUsernameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }
    @Override
    public String registerEmailVerityCode(String type, String email, String ip) {
        synchronized (ip.intern()){
            if(!varifyLimit(ip)) {
                return "请求频繁,请稍后访问";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String,Object> data = Map.of("type",type,"email",email,"code",code);
            amqpTemplate.convertAndSend("mail",data);
            stringRedisTemplate.
                    opsForValue().
                    set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = this.getEmailVerifyCode(email);
        if(code == null)  return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码输入错误,请重新输入";
        if(this.existsAccountByEmail(email)) return "此电子邮件已被其他用户注册";
        if(this.existsAccountByUsername(username)) return "此用户名已被注册,请更换用户名";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null,username,password,email,"user",null, new Date());
        if (this.save(account)) {
             this.deleteEmailVerifyCode(email);
             return null;
        }else{
            return "内部错误,请联系管理员";
        }


    }

    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String code = this.getEmailVerifyCode(vo.getEmail());
        if(code == null){
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())){
            return "验证码输入有误,请重新输入";
        }
        return null;
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo){
        String email = vo.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null){
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())){
            return "验证码输入有误,请重新输入";
        }
        this.deleteEmailVerifyCode(email);
        Account account = this.findAccountByUsernameOrEmail(email);
        if(account == null){
            this.update()
                    .set("email",email)
                    .eq("id",id)
                    .update();
            return null;
        }
        return "该邮件已被其他账号使用,无法完成此操作";
    }

    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id",id).one().getPassword();
        if(!passwordEncoder.matches(vo.getPassword(), password)){
            return "原密码错误,请重新输入!";
        }
        boolean success = this.update()
                .eq("id",id)
                .set("password",passwordEncoder.encode(vo.getNew_password()))
                .update();
        return success ? null : "未知错误,请联系管理员";
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        String varify = this.resetConfirm(new ConfirmResetVO(email,vo.getCode()));
        if(varify != null) return  varify;
        String password = passwordEncoder.encode(vo.getPassword());
        boolean update = this.update().eq("email",email).
                set("password",password).
                update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }

        return null;
    }

    public Account findAccountByUsernameOrEmail(String text) {
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id",id).one();
    }

    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }

    private boolean existsAccountByUsername(String Username ) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("Username",Username));
    }

    private String getEmailVerifyCode(String email) {
        return stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
    }
    private void deleteEmailVerifyCode(String email) {
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
    }
    private boolean varifyLimit(String address){
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flowUtils.limitOnceCheck(key,60);
    }

}
