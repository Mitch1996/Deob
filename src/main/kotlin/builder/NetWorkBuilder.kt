package builder

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import net.ExceptionHandler

object NetWorkBuilder {

    private var bossGroup: EventLoopGroup = NioEventLoopGroup()

    private var workerGroup: EventLoopGroup = NioEventLoopGroup()

    private fun NetWorkBuilder(bossGroup: EventLoopGroup, workerGroup: EventLoopGroup) {
        this.bossGroup = bossGroup
        this.workerGroup = workerGroup
    }

    fun showdown() {
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    fun startUp(serverName: String, port: Int, channelHandler: Class<out ChannelHandler>, gc: Int) {
        val bootstrap = ServerBootstrap()
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel::class.java).childHandler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                val pipeline = ch.pipeline()
                pipeline.addLast("decoder", channelHandler.getDeclaredConstructor().newInstance())
                pipeline.addLast("exception_handler", ExceptionHandler())
            }
        }).childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(port)
    }
}