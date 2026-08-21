package org.example.controller.admin;


import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.EmailRecord;
import org.example.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/email")
public class EmailAdminController {
    @Resource
    EmailService service;

    @GetMapping("/list")
    public RestBean<List<EmailRecord>> listEmailRecord(){
        return RestBean.success(service.listEmailRecord());
    }
}
