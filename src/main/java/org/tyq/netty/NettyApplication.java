package org.tyq.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tyq.netty.webSocket.WebSocketServer;

@RestController
@SpringBootApplication
@EnableScheduling
public class NettyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyApplication.class, args);
		try {
			new WebSocketServer().run(7397);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/hello")
	public String hello(){
		return "this is NettyApplication";
	}
}
