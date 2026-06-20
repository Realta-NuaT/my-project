package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.dto.StoreImage;

@Mapper
public interface ImageStoreMapper extends BaseMapper<StoreImage> {
}
