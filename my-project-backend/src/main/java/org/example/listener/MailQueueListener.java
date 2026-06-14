package org.example.listener;


import jakarta.annotation.Resource;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {
    @Resource
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String,Object> data){
        String email=(String) data.get("email");
        if (email == null || !email.contains("@")) {
            throw new AmqpRejectAndDontRequeueException("无效邮箱");
        }
        Integer code=(Integer) data.get("code");
        String type=(String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register"->
                createMessage("欢迎注册我们的网站",
                        "您的邮件注册码为: "+code+" 为了保障您的安全,请勿泄露验证码给他人",
                        email
                );
            case "reset"->
                createMessage("你的密码重置邮件",
                        "您的邮件重置码为: "+code+" 为了保障您的安全,请勿泄露验证码给他人",
                        email
                );
            case "modify"->
                createMessage("您的邮件修改验证邮件",
                        "您好,您正在绑定新的电子邮件地址,验证码: "+code+" ,有限时间三分钟,如非本人操作,请无视",
                        email
                );
            default -> null;
        };
        if(message==null) return;
        mailSender.send(message);
    }
    private SimpleMailMessage createMessage(String title,String content,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }

}
