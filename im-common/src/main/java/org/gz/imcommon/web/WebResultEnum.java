package org.gz.imcommon.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author guozhong
 * @date 2025/11/15
 */
@Getter
@AllArgsConstructor
public enum WebResultEnum {
    /**
     *  处理成功
     */
    SUCCESS(200,"处理成功"),
    /**
     *  处理失败
     */
    FAIL(400,"处理失败");

    private final Integer code;
    private final String msg;

}
