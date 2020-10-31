package builder

import net.login.LoginDecoder

object GameBuilder {

    fun start() {
        NetWorkBuilder.startUp("Runescape", 43594, LoginDecoder::class.java, 5)
    }
}