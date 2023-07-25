package com.hyx.lansocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SocketMsg {

    private String uid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "UTC")
    private Date date;

    private String ip;

    public Object data;

}
