package org.gz.imcommon.exception;

/**
 * @author 17853
 * 业务异常
 */
public class BizException extends RuntimeException {
    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String message;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
