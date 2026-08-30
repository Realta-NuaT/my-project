package org.example.service;

import com.baomidou.mybatisplus.spring.service.IService;
import org.example.entity.dto.AccountPrivacy;
import org.example.entity.vo.request.PrivacySaveVO;

public interface AccountPrivacyService extends IService<AccountPrivacy> {
     void savePrivacy(int id, PrivacySaveVO vo);
     AccountPrivacy accountPrivacy(int id);
}
