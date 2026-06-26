package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Interact;
import org.example.entity.dto.Topic;
import org.example.entity.dto.TopicType;
import org.example.entity.vo.request.TopicCreateVO;
import org.example.entity.vo.response.TopicDetailVO;
import org.example.entity.vo.response.TopicPreviewVO;
import org.example.entity.vo.response.TopicTopVO;

import java.util.List;

public interface TopicService extends IService<Topic> {
    List<TopicType> listTypes();
    String createTopic(int uid, TopicCreateVO vo);
    List<TopicPreviewVO> listTopicByPage(int page,int type);
    List<TopicTopVO> listTopTopics();
    TopicDetailVO getTopic(int tid);
    void interact(Interact interact, boolean state);
}
