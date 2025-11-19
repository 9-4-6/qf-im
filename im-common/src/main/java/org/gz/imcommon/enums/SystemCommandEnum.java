package org.gz.imcommon.enums;

import lombok.Getter;

@Getter
public enum SystemCommandEnum {
    /**
     * 登录
     */
    LOGIN(0),
    /**
     * 登录 ack
     */
    LOGIN_ACK(1),
    //登出
    LOGOUT(2),
    ;
    private int command;

    SystemCommandEnum(int command){
        this.command=command;
    }
}
