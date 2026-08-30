package org.example.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.entity.dto.AccountPrivacy;
import org.example.entity.vo.request.PrivacySaveVO;
import org.example.mapper.AccountPrivacyMapper;
import org.example.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    @Override
    @Transactional()
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.isStatus();
        switch (vo.getType()) {
            case "phone" -> privacy.setPhone(status);
            case "email" -> privacy.setEmail(status);
            case "wx" -> privacy.setWx(status);
            case "qq" -> privacy.setQq(status);
            case "gender" -> privacy.setGender(status);
        }
        this.saveOrUpdate(privacy);
    }
    @Override
    public AccountPrivacy accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }
}
