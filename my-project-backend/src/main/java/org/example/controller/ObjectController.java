package org.example.controller;


import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.RestBean;
import org.example.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ObjectController {

    @Resource
    ImageService service;

    @GetMapping("/images/avatar/**")
    public void imagesFetch(HttpServletRequest req, HttpServletResponse res) throws Exception{
        this.fetchImage(req, res);
    }
    private void fetchImage(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String imagePath = req.getServletPath().substring(7);
        ServletOutputStream stream = res.getOutputStream();
        if(imagePath.length() <= 13){
            res.setStatus(404);
            stream.println(RestBean.failure(404,"Not found").toString());
        }else{
            try {
                service.fetchImageFromMinio(stream,imagePath);
                res.setHeader("Cache-Control","max-age=2592000");
            } catch (ErrorResponseException e) {
                if(e.response().code() == 404){
                    res.setStatus(404);
                    stream.println(RestBean.failure(404,"Not found").toString());
                }else{
                    log.info("从Minio过去图片异常: "+e.getMessage(),e);
                }
            }
        }
    }
}
