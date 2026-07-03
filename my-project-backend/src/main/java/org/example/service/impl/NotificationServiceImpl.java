package org.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Notification;
import org.example.entity.vo.response.NotificationVO;
import org.example.mapper.NotificationMapper;
import org.example.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.stream;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid))
            .stream()
                .map(notification -> notification.asViewObject(NotificationVO.class))
                .toList();
    }

    public void deleteUserNotification(int id, int uid){
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    public void deleteUserAllNotification(int uid){
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        notification.setTime(new Date());
        this.save(notification);
    }
}
