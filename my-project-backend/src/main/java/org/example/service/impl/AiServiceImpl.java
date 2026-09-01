package org.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.example.service.AiService;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Resource
    ChatModel chatModel;

    @Resource
    FlowUtils utils;

    @Value("${spring.ai.deepseek.frequency}")
    Integer frequency;

    @Value("${spring.ai.deepseek.period}")
    Integer period;

    @Value("${spring.ai.deepseek.block}")
    Integer block;

    @Override
    public SseEmitter chatWithAi(JSONArray context, String ip) {
        String counterKey = Const.FLOW_LIMIT_AI_COUNTER + ip;
        String blockKey = Const.FLOW_LIMIT_AI_BLOCK + ip;
        SseEmitter emitter = new SseEmitter(10000L);
        if (!utils.limitPeriodCheck(counterKey, blockKey, block, frequency, period)) {
            try {
                emitter.send(SseEmitter.event().data("猫姬也会累的,请稍后再来"));
                emitter.complete();  // 正常结束
            } catch (Exception e) {
                // 忽略或尝试 completeWithError，但要防止二次异常
                try {
                    emitter.completeWithError(e);
                } catch (Exception ignored) {
                }
            }
            return emitter;
        }
        List<? extends AbstractMessage> list = context.stream().map(item -> {
            JSONObject obj = JSONObject.from(item);
            return switch (obj.getString("type")) {
                case "user" -> new UserMessage(obj.getString("text"));
                case "assistant" -> new AssistantMessage(obj.getString("text"));
                default -> throw new RuntimeException();
            };
        }).toList();
        Prompt prompt = new Prompt(list.toArray(new Message[0]));
        Flux<ChatResponse> flux = chatModel.stream(prompt);
        flux.subscribe(message -> {
            String text = message.getResult().getOutput().getText();
            try {
                emitter.send(SseEmitter.event().data(text));
            }catch (Exception e){
                emitter.completeWithError(e);
            }

        },emitter::completeWithError, emitter::complete);

        return emitter;
    }
}
