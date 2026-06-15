package org.example.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.RestBean;
import org.example.service.ImageService;
import org.example.utils.Const;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    ImageService service;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestParam("file") MultipartFile file) throws IOException {


        if(file.getSize() > 1024*100)
            return RestBean.failure(400,"头像图片不能大于100KB");
        log.info("正在进行头像上传操作...");
        String url = service.uploadAvatar(file, id);
        if(url!=null) {
            log.info("头像上传成功,大小: "+file.getSize());
            return RestBean.success(url);
        }else{
            return RestBean.failure(400,"头像上传失败,请联系管理员!");
        }
    }
}
