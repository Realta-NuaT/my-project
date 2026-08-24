package org.example.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.MDC;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PageRestBean<T>(long id, int code, PageList<T> data, String message) {
    record PageList<T>(List<T> list,long total, long page){}

    public static <T> PageRestBean<T> success(List<T> list,long total, long page) {
        return new PageRestBean<>(requestId(), 200, new PageList<>(list, total, page), "请求成功");
    }

    public static <T> PageRestBean<T> success(Page<T> page) {
        return success(page.getRecords(), page.getTotal(), page.getCurrent());
    }

    public static long requestId() {
        String requestId = Optional.ofNullable(MDC.get("reqID")).orElse("0");
        return Long.parseLong(requestId);
    }
}
