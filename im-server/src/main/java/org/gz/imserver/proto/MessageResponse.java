package org.gz.imserver.proto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 17853
 * */
@Data
public class MessageResponse<T> implements Serializable {
    private int command;

    private T data;

}
