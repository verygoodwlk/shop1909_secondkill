package com.qf.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WsMsgEntity<T> implements Serializable {

    private Integer fromid = -1;//发送方 -1 系统
    private Integer toid;//接收方
    private Integer type;//消息类型 1-初始化消息 2-心跳消息 3-秒杀提醒消息
    private T data;//消息携带数据
}
