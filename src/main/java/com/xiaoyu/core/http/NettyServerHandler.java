package com.xiaoyu.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.core.context.DispatchStation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

	Logger logger = LoggerFactory.getLogger("NettyServerHandler");
	private DispatchStation dispatcher;

	public NettyServerHandler(DispatchStation dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest request = null;
		if (msg instanceof HttpObject)
			request = (FullHttpRequest) msg;
		logger.info("uri:" + request.uri());
		// for (Entry<String, String> entry : request.headers()) {
		// System.out.println("HEADER: " + entry.getKey() + '=' +
		// entry.getValue() + "\r\n");
		// }

		// if(HttpMethod.GET.equals(request.method())){
		//
		// }
		// else if(HttpMethod.POST.equals(request.method())){
		//
		// }
		/*
		 * String s = "来源：知乎" + "著作权归作者所有，转载请联系作者获得授权。" + "我外公和我外婆真的是两个极端。" +
		 * "小时候学琴，学琴的地方门口有一家饭店卖臭豆腐煲，" +
		 * "结果一回家，我外公的眼神就会很绝望，问我你们到底在哪里上课，你就不能吃点别的吗？" +
		 * "下了课外婆就会带我去吃不过好在我外婆也就热爱吃个羊肉和臭豆腐，而且我家是外公掌勺，所以在我上大学之前，饮食结构都基本单一。外公不吃无鳞鱼，不吃牛羊肉，不吃鹅，不吃任何动物"
		 * + "。直到我高中的时候第一次吃猪大肠……" +"卧槽！怎么会那么好吃。那个臭豆腐煲真的" +
		 * "是每次都要捏着鼻子去吃，然而吃到嘴里的时候鲜的眉毛都要掉了，咸淡恰到好处，一端上来就迫不及待地吹两下，也顾不得烫嘴就吃了进去。"
		 * +"那一次我回家以后外公很高兴，问我说要吃什么，去下馆子。" +
		 * "公用一种你都经历了些什么的眼神看着我，说我都不想说你，哎呀那个猪大肠就是" +
		 * "后在家我就不作妖了，不过我外公的红烧肉小炒肉做的真的好吃，结果在上海呆";
		 */

		FullHttpResponse response = dispatcher.dispatch(request);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
		ctx.channel().writeAndFlush(response);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().close();
	}

}
