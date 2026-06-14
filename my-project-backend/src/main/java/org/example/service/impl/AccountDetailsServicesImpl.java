package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Account;
import org.example.entity.dto.AccountDetails;
import org.example.entity.vo.request.DetailsSaveVO;
import org.example.mapper.AccountDetailsMapper;
import org.example.service.AccountDetailsServices;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServicesImpl extends ServiceImpl<AccountDetailsMapper,AccountDetails> implements AccountDetailsServices {

    @Resource
    AccountService service;

    @Override
    public AccountDetails findAccountDetailsById(int id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVO vo) {
        Account account = service.findAccountByUsernameOrEmail(vo.getUsername());
        if(account == null || account.getId() ==id){
            if(service.update()
                    .eq("id",id)
                    .set("username",vo.getUsername())
                    .update()){
                this.saveOrUpdate(new AccountDetails(
                        id,vo.getGender(),vo.getPhone(),vo.getQq(),vo.getWx(),vo.getDesc()
                ));
                return true;
            }else{
                System.out.println("坐标:AccountDetailsServicesImpl 原因:saveAccountDetails函数出错");
            }
        }
        return false;
    }
}
