package com.rain.netty;
import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

public class NettyHttpHandler extends SimpleChannelInboundHandler<HttpObject> {

	private boolean DEBUG = true;
	
	private HttpRequest mRequest = null;
	private boolean readingChunks;
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk
	 
    private HttpPostRequestDecoder decoder;

	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	System.err.println("NettyHttpHandler - channelInactive()");
    	
        
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		
		System.out.println("NettyHttpHandler - channelRead0()");
		
		this.messageReceived(ctx, msg);
	}

	public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		
		System.out.println("NettyHttpHandler - messageReceived()");
		
		System.out.println("message class name = " + msg.getClass().getName());
		System.out.println("DiskFileUpload.baseDirectory = " + DiskFileUpload.baseDirectory);
		
		if (msg instanceof HttpRequest) {
			
			System.out.println("Message type - HttpRequest");
			
			mRequest = (HttpRequest) msg;
			
			String uri = mRequest.getUri();
			HttpMethod method = mRequest.getMethod();
			
			System.out.println("Uri: " + uri);
			System.out.println("Method: " + method);

			// new getMethod
            Set<Cookie> cookies;
            String value = mRequest.headers().get(COOKIE);
            if (value == null) {
                /**
                 * Returns an empty set (immutable).
                 */
                cookies = Collections.emptySet();
            } else {
                cookies = CookieDecoder.decode(value);
            }
            for (Cookie cookie : cookies) {
                System.out.println("COOKIE: " + cookie.toString());
            }
 
            QueryStringDecoder decoderQuery = new QueryStringDecoder(mRequest.getUri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                	System.out.println("URI: " + attr.getKey() + '=' + attrVal);
                }
            }

			if (method.equals(HttpMethod.GET))
			{
				String response = "{\"FLAG\":\"10000\"}";
            	if (DEBUG)
                {
                	ProcessDebug.loadDefaultDebugData();
                	response = ProcessDebug.processDebugData(mRequest.getUri());
                }
            	writeResponse(ctx.channel(), uri+"="+response);
			}
			else if (method.equals(HttpMethod.POST))
			{
                System.err.println("===this is http post===");
                try {
                    /**
                     * 通过HttpDataFactory和request构造解码器
                     */
                    decoder = new HttpPostRequestDecoder(factory, mRequest);
                } catch (ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    //responseContent.append(e1.getMessage());
                    writeResponse(ctx.channel(), "{\"FLAG\":\"10001\"}");
                    ctx.channel().close();
                    return;
                }
 
                readingChunks = HttpHeaders.isTransferEncodingChunked(mRequest);
                System.out.println("Is Chunked: " + readingChunks + "\r\n");
                System.out.println("IsMultipart: " + decoder.isMultipart() + "\r\n");
                if (readingChunks) {
                    // Chunk version
                	System.out.println("Chunks: ");
                    readingChunks = true;
                }

				//writeResponse(ctx.channel(), "{\"FLAG\":\"10000\"}");
			}
		}
		
		if (decoder != null) {
            if (msg instanceof HttpContent) {
                // New chunk is received
                HttpContent chunk = (HttpContent) msg;
                try {
                    decoder.offer(chunk);
                } catch (ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    writeResponse(ctx.channel(), "{\"FLAG\":\"10002\"}");
                    ctx.channel().close();
                    return;
                }

                try {
                    while (decoder.hasNext()) {
                        InterfaceHttpData data = decoder.next();
                        if (data != null) {
                            try {
                                writeHttpData(data);
                            } finally {
                                data.release();
                            }
                        }
                    }
                } catch (EndOfDataDecoderException e1) {
                	//writeResponse(ctx.channel(), "{\"FLAG\":\"10003\"}");
                }
 
                // example of reading only if at the end
                if (chunk instanceof LastHttpContent) {
                	String response = "{\"FLAG\":\"10000\"}";
                	if (DEBUG)
                    {
                    	ProcessDebug.loadDefaultDebugData();
                    	response = ProcessDebug.processDebugData(mRequest.getUri());
                    }
                	writeResponse(ctx.channel(), response);
                    readingChunks = false;
                    reset();
                }
            }
        }
	}

	private void reset() {
		mRequest = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }
 
    private void writeHttpData(InterfaceHttpData data) {
        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return;
            }
            if (value.length() > 100) {
            	System.out.println("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " data too long\r\n");
            } else {
            	System.out.println("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.toString() + "\r\n");
            }
        }
    }

    
	private void writeResponse(Channel channel, String responseValue) {
		// Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseValue, CharsetUtil.UTF_8);
 
        // Decide whether to close the connection or not.
        boolean close = mRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
                || mRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !mRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
 
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
 
        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }
 
        Set<Cookie> cookies;
        String value = mRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = CookieDecoder.decode(value);
        }
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (Cookie cookie : cookies) {
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
            }
        }
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
	}
	
}
