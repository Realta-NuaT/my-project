package org.example.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.entity.PageRestBean;
import org.example.entity.RestBean;
import org.example.entity.dto.EmailRecord;
import org.example.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/email")
public class EmailAdminController {
    @Resource
    EmailService service;

    @GetMapping("/list")
    public PageRestBean<EmailRecord> listEmailRecord(@RequestParam int page,
                                                                   @RequestParam int size) {
        return PageRestBean.success(service.listEmailRecord(page, size));
    }

    @GetMapping("/resend")
    public RestBean<Void> resendEmailRecord(@RequestParam int id) {
        if (service.resendEmailRecord(id)) {
            return RestBean.success();
        }else{
            return RestBean.failure(400, "邮件重发失败");
        }
    }
}
