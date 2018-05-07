package org.tyq.netty.webSocket;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tyq.netty.entity.TuLingRequest;
import org.tyq.netty.entity.TuLingRespose;
import org.tyq.netty.enums.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class.getName());
    private WebSocketServerHandshaker handshaker;
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        //websocket接入
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        group.add(ctx.channel());
        System.out.println("客户端"+ctx.channel().id()+"与服务端连接开启");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        group.remove(ctx.channel());
        System.out.println("客户端"+ctx.channel().id()+"与服务端连接关闭");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        //如果HTTP解码失败，返回HTTP异常
        if (!req.getDecoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        String url = "ws://"+req.headers().get(HttpHeaders.Names.HOST);
        //构造握手相应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(url, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        //判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            System.out.println(new Date()+"客户端【"+ctx.channel().id()+"】发送了有个Ping...");
            ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        //本例仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            System.out.println("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        /**
         * 获取客户端发送的消息
         */
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("客户端"+ctx.channel().id()+"发送消息:"+request);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("%s received %s", ctx.channel(), request));
        }
        /**
          *返回数据到客户端
          */
        //ctx.channel().writeAndFlush(new TextWebSocketFrame(request + ",欢迎使用Netty WebSocket服务,现在时刻:" + new Date().toString()));
        //String json = "{'name':'"+ctx.channel().id()+"','text':'"+request+"'}";
        //TextWebSocketFrame tws = new TextWebSocketFrame(json);
        TextWebSocketFrame twsf = new TextWebSocketFrame("{'name':'时间【"+new Date().toString()+"】客户【"+ctx.channel().id()+"】','text':'"+request+"'}");
        /**
         * 群发
         */
        //group.writeAndFlush(twsf);
        /**
         * 返回【谁发的返回给谁】
         */
        ctx.channel().writeAndFlush(twsf);
        //调用图灵机器人自动回复
        TuLingRequest agrs = new TuLingRequest();
        agrs.getPerception().getInputText().setText(request);
        TextWebSocketFrame serverTwsf;
        try {
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            String str = restTemplate.postForObject("http://openapi.tuling123.com/openapi/api/v2", agrs, String.class);
            TuLingRespose tuLingRespose = JSONObject.parseObject(str,TuLingRespose.class);
            logger.info(tuLingRespose.toString());
            if(!ErrorCode.isHave(tuLingRespose.getIntent().getCode())){
                for(TuLingRespose.Results result : tuLingRespose.getResults()){
                    serverTwsf = new TextWebSocketFrame("{'name':'时间【" + new Date().toString() + "】服务器','text':'" + result.getValues().getText() + "'}");
                    ctx.channel().writeAndFlush(serverTwsf);
                }
            }else {
                serverTwsf = new TextWebSocketFrame("{'name':'时间【"+new Date().toString()+"】服务器','text':'不好意思，出了点状况！！！'}");
                ctx.channel().writeAndFlush(serverTwsf);
            }
        }catch (Exception e){
            logger.info("调用图灵api失败："+e);
            serverTwsf = new TextWebSocketFrame("{'name':'时间【"+new Date().toString()+"】服务器','text':'不好意思，出了点状况！！！'}");
            ctx.channel().writeAndFlush(serverTwsf);
            e.printStackTrace();
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse resp) {
        //返回应答给客户端
        if (resp.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(resp.getStatus().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
        }
        //如果是非Kepp-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(resp);
        if (resp.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void sendHello(){
        for (Channel channel : group){
            System.out.println("服务器发出慰问！！！");
            channel.writeAndFlush( new TextWebSocketFrame( "{'name':'时间【"+new Date().toString()+"】服务器','text':'hello "+channel.id()+"'}"));
        }
    }
}
