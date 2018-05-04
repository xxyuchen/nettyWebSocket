package org.tyq.netty.scheduled;

import org.springframework.stereotype.Component;
import org.tyq.netty.webSocket.WebSocketServerHandler;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2018/3/14 0014.
 */
@Component
public class ATask{

    @Resource
    private WebSocketServerHandler webSocketServerHandler;

    //@Scheduled(cron = "0/5 * * * * ?")
    public void scheduleTask() {
        System.out.println("服务器发出慰问！！！");
        webSocketServerHandler.sendHello();
    }
}
