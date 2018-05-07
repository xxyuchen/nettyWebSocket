package org.tyq.netty.demo;

import com.alibaba.fastjson.JSONObject;
import org.tyq.netty.entity.TuLingRespose;

/**
 * Created by Administrator on 2018/5/7 0007.
 */
public class Demo {

    public static void main(String[] args){
        String str ="{\"emotion\":{\"robotEmotion\":{\"a\":0,\"d\":0,\"emotionId\":0,\"p\":0},\"userEmotion\":{\"a\":0,\"d\":0,\"emotionId\":0,\"p\":0}},\"intent\":{\"actionName\":\"\",\"code\":10004,\"intentName\":\"\"},\"results\":[{\"groupType\":0,\"resultType\":\"text\",\"values\":{\"text\":\"人见人爱的yuchen，记住了吧～\"}},{\"groupType\":0,\"resultType\":\"text\",\"values\":{\"text\":\"那你的呢？\"}}]}";
        TuLingRespose tuLingRespose = JSONObject.parseObject(str, TuLingRespose.class);
        System.out.println(tuLingRespose.toString());
    }
}
