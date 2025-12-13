package org.gz.imcommon.web;

/**
 * @author guozhong
 * @date 2025/11/15
 */
public class WebResultBuilder {
    /**
     * 构建处理成功请求
     * @param data
     * @param <T>
     * @return
     */
    public static <T> WebResult success(T data){
        WebResultEnum success = WebResultEnum.SUCCESS;
        return WebResult.builder().success(true).code(success.getCode())
                .msg(success.getMsg()).data(data).build();
    }

    /**
     * 构建处理成功请求
     * @param code 响应代码
     * @param msg 响应消息
     * @param data 响应数据
     * @return
     */
    public static <T> WebResult success(Integer code, String msg, T data){
        return WebResult.builder().success(true).code(code)
                .msg(msg).data(data).build();
    }

    /**
     * 构建处理失败请求
     * @param
     * @param <T>
     * @return
     */
    public static <T> WebResult fail(){
        WebResultEnum fail = WebResultEnum.FAIL;
        return WebResult.builder().success(false).code(fail.getCode())
                .msg(fail.getMsg()).build();
    }

    /**
     * 构建自定义参数的处理失败请求
     * @param code 响应代码
     * @param msg 响应消息
     * @return
     */
    public static <T> WebResult fail(Integer code, String msg){
        return WebResult.builder().success(false).code(code)
                .msg(msg).build();
    }
}
