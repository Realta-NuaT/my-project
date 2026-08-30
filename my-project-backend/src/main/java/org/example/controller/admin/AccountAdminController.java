package org.example.controller.admin;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.dto.AccountDetails;
import org.example.entity.dto.AccountPrivacy;
import org.example.entity.vo.response.AccountVO;
import org.example.service.AccountDetailsServices;
import org.example.service.AccountPrivacyService;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/user")
public class AccountAdminController {

    @Resource
    AccountService service;

    @Resource
    AccountDetailsServices detailsServices;

    @Resource
    AccountPrivacyService  privacyServices;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @GetMapping("/list")
    public RestBean<JSONObject> accountList(@RequestParam int page,
                                            @RequestParam int size,
                                            @RequestParam(required = false) String keyWord) {
        JSONObject object = new JSONObject();
        Page<Account> accountPage = service.page(Page.of(page, size), Wrappers.<Account>query()
                .eq(keyWord != null, "id", keyWord).or()
                .like(keyWord != null, "username", "%" + keyWord + "%")
        );
        List<AccountVO> list = accountPage
                .getRecords()
                .stream()
                .map(a -> a.asViewObject(AccountVO.class))
                .toList();
        object.put("total", accountPage.getTotal());
        object.put("list", list);
        return RestBean.success(object);
    }

    @GetMapping("/detail")
    public RestBean<JSONObject> accountDetail(int id){
        JSONObject object = new JSONObject();
        object.put("detail", detailsServices.findAccountDetailsById(id));
        object.put("privacy",privacyServices.accountPrivacy(id));
        return RestBean.success(object);
    }

    @PostMapping("save")
    public RestBean<Void> saveAccount(@RequestBody JSONObject object,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid ){
        int id = object.getInteger("id");
        if(uid == id)
            return RestBean.failure(400, "不能修改自己的账号信息");
        Account account = service.findAccountById(id);
        Account save = object.toJavaObject(Account.class);
        handleBanned(account, save);
        BeanUtils.copyProperties(save, account, "password", "registerTime");
        service.saveOrUpdate(account);
        handleBanned(account, save);
        AccountDetails details = detailsServices.findAccountDetailsById(id);
        AccountDetails saveDetails = object.getJSONObject("detail").toJavaObject(AccountDetails.class);
        BeanUtils.copyProperties(saveDetails, details);
        detailsServices.saveOrUpdate(details);

        AccountPrivacy  privacy = privacyServices.accountPrivacy(id);
        AccountPrivacy savePrivacy = object.getJSONObject("detail").toJavaObject(AccountPrivacy.class);
        BeanUtils.copyProperties(savePrivacy, privacy);
        privacyServices.saveOrUpdate(privacy);

        return RestBean.success();
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody JSONObject object){
        service.modifyPassword(
                object.getInteger("id"),
                object.getString("newPassword")
        );
        return RestBean.success();
    }

    private void handleBanned(Account old, Account current){
        String key = Const.BANNED_BLOCK + old.getId();
        if(!old.isBanned() && current.isBanned()){
            template.opsForValue().set(key, "true", expire, TimeUnit.HOURS);
        }else if(old.isBanned() && !current.isBanned()){
            template.delete(key);
        }
    }
}
