package net.login

import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.message.MessageDecoder

class LoginDecoder : MessageDecoder<Channel>() {

    override fun handle(t: Channel, pkt: Int) {
        TODO("Not yet implemented")
    }

    override fun decode(ctx: ChannelHandlerContext, bytebuf: ByteBuf, out: MutableList<Any>) {
        super.decode(ctx, bytebuf, out)
    }
}