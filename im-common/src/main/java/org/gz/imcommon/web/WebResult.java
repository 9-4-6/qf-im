package org.gz.imcommon.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author guozhong
 * @date 2025/11/15
 * @description 统一响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 8456352400486508357L;
    private Integer code;
    private String msg;
    private Boolean success = true;
    private T data;
}
