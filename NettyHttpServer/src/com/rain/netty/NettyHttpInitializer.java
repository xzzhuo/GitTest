package com.rain.netty;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class NettyHttpInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("NettyHttpInitializer@initChannel()");
		
		ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
        //ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65536));  
        ch.pipeline().addLast(new ChunkedWriteHandler()); 
		//ch.pipeline().addLast("TransferHandler", new TransferHandler());
		ch.pipeline().addLast("NettyHttpHandler", new NettyHttpHandler());
	}

}
