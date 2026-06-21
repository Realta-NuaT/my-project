package org.example.controller;


import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.entity.RestBean;
import org.example.entity.vo.request.TopicCreateVO;
import org.example.entity.vo.response.TopicPreviewVO;
import org.example.entity.vo.response.TopicTypeVO;
import org.example.entity.vo.response.WeatherVO;
import org.example.mapper.TopicTypeMapper;
import org.example.service.TopicService;
import org.example.service.WeatherService;
import org.example.utils.Const;
import org.example.utils.ControllerUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {


    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Resource
    ControllerUtils utils;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude){
        WeatherVO vo = service.fetchWeather(longitude, latitude);
        return vo ==null?
                RestBean.failure(400,"获取地理位置信息与天气失败,请联系管理员"):RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes(){
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type->type.asViewObject(TopicTypeVO.class))
                .toList());
    }
    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo ,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(()->topicService.createTopic(id,vo));
    }
    @GetMapping("list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,@RequestParam @Min(0) int type){
        return
    }
}
