package org.example.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.entity.dto.AccountDetails;
import org.example.entity.vo.request.DetailsSaveVO;

public interface AccountDetailsServices extends IService<AccountDetails> {
    AccountDetails findAccountDetailsById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVO vo);
}
