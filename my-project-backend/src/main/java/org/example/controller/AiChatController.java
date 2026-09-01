package org.example.controller;


import com.alibaba.fastjson2.JSONArray;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.entity.RestBean;
import org.example.service.AiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Resource
    AiService aiService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter charWithAi(@RequestBody JSONArray content, HttpServletRequest request) {
        return aiService.chatWithAi(content, request.getRemoteAddr());
    }

}
