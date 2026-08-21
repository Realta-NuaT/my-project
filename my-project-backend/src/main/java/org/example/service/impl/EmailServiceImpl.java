package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.entity.dto.EmailRecord;
import org.example.mapper.EmailRecordMapper;
import org.example.service.EmailService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    EmailRecordMapper recordMapper;

    @Override
    public void sendVerifyEmail(String type, String email, int code) {
        EmailRecord emailRecord = switch (type){
            case "register"-> new  EmailRecord(email,
                    "欢迎注册我们的网站", "您的邮件注册码为: "+code+" 为了保障您的安全,请勿泄露验证码给他人"
                    );
            case "reset"-> new  EmailRecord(email,
                    "你的密码重置邮件", "您的邮件重置码为: "+code+" 为了保障您的安全,请勿泄露验证码给他人"
            );
            case "modify"-> new  EmailRecord(email,
                    "您的邮件修改验证邮件", "您好,您正在绑定新的电子邮件地址,验证码: "
                    +code+" ,有限时间三分钟,如非本人操作,请无视"
            );
            default -> throw new IllegalArgumentException(type);
        };
        recordMapper.insert(emailRecord);
        amqpTemplate.convertAndSend("mail",emailRecord);
    }
}
