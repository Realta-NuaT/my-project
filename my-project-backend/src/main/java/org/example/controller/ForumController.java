package org.example.controller;


import org.example.entity.RestBean;
import org.example.entity.vo.response.WeatherVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(){

    }
}
