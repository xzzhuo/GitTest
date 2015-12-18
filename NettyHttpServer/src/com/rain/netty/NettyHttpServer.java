package com.rain.netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyHttpServer {
	private static final int port = 8080;
	public static boolean isSSL = true;
	
	public void start() throws InterruptedException
	{
		ServerBootstrap b = new ServerBootstrap();
		EventLoopGroup group = new NioEventLoopGroup();

		try
		{
			b.group(group);
			b.channel(NioServerSocketChannel.class);
			b.localAddress(new InetSocketAddress(port));
			b.childHandler(new NettyHttpInitializer());
			
			ChannelFuture f = b.bind().sync();
			System.out.println(NettyHttpServer.class.getName() + " started and listen on " + f.channel().localAddress());
			f.channel().closeFuture().sync();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			group.shutdownGracefully().sync();
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			new NettyHttpServer().start();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
