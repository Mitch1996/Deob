package net

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class ExceptionHandler : ChannelInboundHandlerAdapter() {

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        if (cause != null) {
            if (cause.stackTrace[0].methodName == "read0")
                cause.printStackTrace()
            ctx?.close()
        }
    }
}