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
    /**
     * 单聊
     */
    SINGLE_CHAT(2),
    //登出
    LOGOUT(20),
    ;
    private int command;

    SystemCommandEnum(int command){
        this.command=command;
    }
}
