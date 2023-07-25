package com.hyx.lansocket;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("msg")
public class MsgController {




    @RequestMapping("listMsg")
    public JSONObject listMsg(){
        JSONObject rtn = new JSONObject();
        rtn.put("code","0");
        rtn.put("data",MsgQueue.getMsg());
        return rtn;
    }
}
