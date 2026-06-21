package org.example.controller;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.dto.AccountDetails;
import org.example.entity.vo.request.ChangePasswordVO;
import org.example.entity.vo.request.DetailsSaveVO;
import org.example.entity.vo.request.ModifyEmailVO;
import org.example.entity.vo.request.PrivacySaveVO;
import org.example.entity.vo.response.AccountDetailsVO;
import org.example.entity.vo.response.AccountPrivacyVO;
import org.example.entity.vo.response.AccountVO;
import org.example.service.AccountDetailsServices;
import org.example.service.AccountPrivacyService;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.ControllerUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    private AccountService service;

    @Resource
    AccountDetailsServices detailsServices;

    @Resource
    AccountPrivacyService privacyServices;

    @Resource
    ControllerUtils utils;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                    HttpServletRequest request){
        Account account = service.findAccountById(id);
        return RestBean.success(account.asViewObject(AccountVO.class));
    }
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute(Const.ATTR_USER_ID) int id){
        AccountDetails details = Optional
                .ofNullable(detailsServices.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }
    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid DetailsSaveVO vo){
        boolean success = detailsServices.saveAccountDetails(id,vo);
        return success?RestBean.success():RestBean.failure(400,"此用户名已被其他用户使用,请更换");

    }
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVO vo){
        return utils.messageHandle(()->service.modifyEmail(id,vo));
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                         @RequestBody @Valid ChangePasswordVO vo){
        return utils.messageHandle(()->service.changePassword(id,vo));
    }
    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid PrivacySaveVO vo) {
        privacyServices.savePrivacy(id, vo);
        return RestBean.success();
    }
    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVO> Privacy(@RequestAttribute(Const.ATTR_USER_ID) int id) {
        return  RestBean.success(privacyServices.accountPrivacy(id).asViewObject(AccountPrivacyVO.class));
    }
}
