package org.example.controller.admin;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.example.entity.PageRestBean;
import org.example.entity.RestBean;
import org.example.entity.vo.response.TopicPreviewVO;
import org.example.service.TopicService;
import org.example.utils.ProhibitedUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/forum")
public class ForumAdminController {

    @Resource
    private TopicService service;

    @Resource
    ProhibitedUtils  prohibitedUtils;

    @GetMapping("/list")
    public PageRestBean<TopicPreviewVO> list(@RequestParam int page,
                                             @RequestParam int size){
        JSONObject object = service.listAllTopicByPage(page, size);
        return PageRestBean.success(
                object.getJSONArray("list").toList(TopicPreviewVO.class),
                object.getIntValue("total"),
                page
        );
    }

    @GetMapping("/delete")
    public RestBean<Void> delete(@RequestParam int tid){
        service.deleteTopic(tid);
        return RestBean.success();
    }

    @PostMapping("/top")
    public RestBean<Void> setTop(@RequestBody JSONObject object){
        service.setTopicTop(
                object.getIntValue("id"),
                object.getBooleanValue("status")
        );
        return  RestBean.success();
    }

    @PostMapping("/locked")
    public RestBean<Void> setLocked(@RequestBody JSONObject object){

        service.setTopicLocked(
                object.getIntValue("id"),
                object.getBooleanValue("status")
        );
        return  RestBean.success();
    }

    @PostMapping("/invisible")
    public RestBean<Void> setInvisible(@RequestBody JSONObject object){
        service.setTopicInvisible(
                object.getIntValue("id"),
                object.getBooleanValue("status")
        );
        return  RestBean.success();
    }

    @GetMapping("/prohibited-list")
    public RestBean<List<String>> getProhibitedWords() {
        return RestBean.success(prohibitedUtils.getProhibitedWords());
    }

    @PostMapping("/prohibited-list-save")
    public RestBean<Void> saveProhibitedList(@RequestBody JSONArray array){
        prohibitedUtils.setProhibitedWords(array.toList(String.class));
        return  RestBean.success();
    }
}
