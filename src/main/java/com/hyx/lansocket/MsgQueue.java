package com.hyx.lansocket;

import java.util.ArrayList;
import java.util.List;

public class MsgQueue {


    private static List<SocketMsg> list = new ArrayList<>();

    public static void addMsg(SocketMsg obj){
        list.add(obj);
    }

    public static List<SocketMsg> getMsg(){
        return list;
    }
}
