package net.message

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

abstract class MessageDecoder<T> : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, bytebuf: ByteBuf, out: MutableList<Any>) {
        //TODO :  Decode the message
    }

    protected abstract fun handle(t: T,  pkt : Int) // TODO : Buffer support
}