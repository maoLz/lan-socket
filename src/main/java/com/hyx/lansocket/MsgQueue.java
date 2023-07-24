package com.hyx.lansocket;

import java.util.ArrayList;
import java.util.List;

public class MsgQueue {


    private static List<Object> list = new ArrayList<>();

    public static void addMsg(Object obj){
        list.add(obj);
    }

    public List<Object> getMsg(){
        return list;
    }
}
