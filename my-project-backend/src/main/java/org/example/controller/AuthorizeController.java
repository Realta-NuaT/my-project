package org.example.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailResetVO;
import org.example.service.AccountService;
import org.example.utils.ControllerUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService accountService;

    @Resource
    ControllerUtils utils;

    @GetMapping("/ask-code")
    public RestBean<Void> askCode(@RequestParam @Email String email,
                                  @RequestParam @Pattern(regexp = "(register|reset|modify)") String type,
                                  HttpServletRequest request){
        return utils.messageHandle(
                ()->accountService.registerEmailVerifyCode(type,email,request.getRemoteAddr())
        );
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return utils.messageHandle(()->accountService.registerEmailAccount(vo));
    }
    @PostMapping("/reset-confirm")
    public  RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return utils.messageHandle(()->accountService.resetConfirm(vo));
    }

    @PostMapping("/reset-password")
    public  RestBean<Void> resetConfirm(@RequestBody @Valid EmailResetVO vo){
        return utils.messageHandle(()->accountService.resetEmailAccountPassword(vo));
    }
}
