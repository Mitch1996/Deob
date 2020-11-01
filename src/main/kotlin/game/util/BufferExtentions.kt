package game.util

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.math.BigInteger

private val STRING_TERMINATOR = 0

enum class ByteModification {

    NONE,

    ADD,

    SUB,

    NEG;
}

enum class ByteOrder {

    LE,

    BE,

    ME,

    IME;
}

fun ByteBuf.encipherRSA(exp: BigInteger, mod: BigInteger): ByteBuf {
    val bytes = ByteArray(readShort().toInt())
    readBytes(bytes)
    return Unpooled.wrappedBuffer(BigInteger(bytes).modPow(exp, mod).toByteArray())
}


fun ByteBuf.decipherXTEA(keys: IntArray): ByteBuf {
    if (keys.size != 4) {
        throw IllegalArgumentException("XTEA needs 4 keys to be deciphered")
    }
    val bytes = ByteArray(readableBytes())
    readBytes(bytes)
    val xteaBuffer = Unpooled.wrappedBuffer(bytes)
    xteaBuffer.decipherXTEA(keys, 0, bytes.size)
    return xteaBuffer
}

private fun ByteBuf.decipherXTEA(keys: IntArray, start: Int, end: Int) {
    if (keys.size != 4) {
        throw IllegalArgumentException("XTEA needs 4 keys to be deciphered")
    }
    val numQuads = (end - start) / 8
    for (i in 0 until numQuads) {
        var sum = -0x61c88647 * 32
        var v0 = getInt(start + i * 8)
        var v1 = getInt(start + i * 8 + 4)
        for (j in 0..31) {
            v1 -= (v0 shl 4 xor v0.ushr(5)) + v0 xor sum + keys[sum.ushr(11) and 3]
            sum -= -0x61c88647
            v0 -= (v1 shl 4 xor v1.ushr(5)) + v1 xor sum + keys[sum and 3]
        }
        setInt(start + i * 8, v0)
        setInt(start + i * 8 + 4, v1)
    }

    fun ByteBuf.readString(): String {
        val sb = StringBuilder()
        var b: Byte
        while (isReadable) {
            b = readByte()
            if (b.toInt() == 0) {
                break
            }
            sb.append(b.toChar())
        }
        return sb.toString()
    }

    fun ByteBuf.readJagString(): String {
        val sb = StringBuilder()
        var b: Byte
        readByte()
        while (isReadable) {
            b = readByte()
            if (b.toInt() == 0) {
                break
            }
            sb.append(b.toChar())
        }
        return sb.toString()
    }

    fun ByteBuf.toByteArray(amount: Int = readableBytes()): ByteArray {
        val bytes = ByteArray(amount)
        for (i in 0 until amount) {
            bytes[i] = readByte()
        }
        return bytes
    }

    fun ByteBuf.readByte(mod: ByteModification = ByteModification.NONE): Int {
        var value = readByte().toInt()
        when (mod) {
            ByteModification.ADD -> value += 128
            ByteModification.SUB -> value -= 128
            ByteModification.NEG -> value = -value
            else -> {
            }
        }
        return value
    }

    fun ByteBuf.writeByte(value: Int, modification: ByteModification = ByteModification.NONE) {
        var temp = value
        when (modification) {
            ByteModification.ADD -> temp += 128
            ByteModification.NEG -> temp = -temp
            ByteModification.SUB -> temp = 128 - value
            ByteModification.NONE -> {
            }
        }
        writeByte(temp)
    }

    fun ByteBuf.readUnsignedByte(mod: ByteModification = ByteModification.NONE): Int {
        var value = readByte().toInt()
        when (mod) {
            ByteModification.ADD -> value += 128
            ByteModification.SUB -> value = 128 - value
            ByteModification.NEG -> value = -value
            else -> {
            }
        }
        return value and 0xFF
    }


    fun ByteBuf.readShort(mod: ByteModification = ByteModification.NONE): Int {
        var value = 0
        value = value or (readUnsignedByte().toInt() shl 8)
        value = value or readUnsignedByte(mod)
        return value
    }

    fun ByteBuf.readShortLE(mod: ByteModification = ByteModification.NONE): Int {
        var value = 0
        value = value or readUnsignedByte(mod)
        value = value or (readUnsignedByte().toInt() shl 8)
        return value
    }

    fun ByteBuf.readUnsignedShort(mod: ByteModification = ByteModification.NONE): Int {
        var value = 0
        value = value or (readUnsignedByte().toInt() shl 8)
        value = value or readUnsignedByte(mod)
        return value and 0xFFFF
    }

    fun ByteBuf.writeShort(value: Int, modification: ByteModification = ByteModification.NONE, order: ByteOrder = ByteOrder.BE) {
        when (order) {
            ByteOrder.BE -> {
                writeByte(value shr 8)
                writeByte(value, modification)
            }

            ByteOrder.LE -> {
                writeByte(value, modification)
                writeByte(value shr 8)
            }
            else -> throw IllegalStateException("$order short is not possible!")
        }
    }

    fun ByteBuf.readInt(mod: ByteModification = ByteModification.NONE): Int {
        var value = 0
        value = value or (readUnsignedByte().toInt() shl 24)
        value = value or (readUnsignedByte().toInt() shl 16)
        value = value or (readUnsignedByte().toInt() shl 8)
        value = value or readUnsignedByte(mod)
        return value
    }

    fun ByteBuf.readUnsignedSmart(): Int {
        val peek = getByte(readerIndex()).toInt() and 0xFF
        return if (peek < 128) readUnsignedByte().toInt() else readUnsignedShort() - 32768
    }

    fun ByteBuf.readUnsignedInt(mod: ByteModification = ByteModification.NONE): Long {
        var value = 0
        value = value or (readUnsignedByte().toInt() shl 24)
        value = value or (readUnsignedByte().toInt() shl 16)
        value = value or (readUnsignedByte().toInt() shl 8)
        value = value or readUnsignedByte(mod)
        return value.toLong() and 0xFFFFFFFFL
    }

    fun ByteBuf.writeInt(
            value: Int,
            modification: ByteModification = ByteModification.NONE,
            order: ByteOrder = ByteOrder.BE
    ) {
        when (order) {
            ByteOrder.BE -> {
                writeByte(value shr 24)
                writeByte(value shr 16)
                writeByte(value shr 8)
                writeByte(value, modification)
            }

            ByteOrder.ME -> {
                writeByte(value shr 8)
                writeByte(value, modification)
                writeByte(value shr 24)
                writeByte(value shr 16)
            }

            ByteOrder.IME -> {
                writeByte(value shr 16)
                writeByte(value shr 24)
                writeByte(value, modification)
                writeByte(value shr 8)
            }

            ByteOrder.LE -> {
                writeByte(value, modification)
                writeByte(value shr 8)
                writeByte(value shr 16)
                writeByte(value shr 24)
            }
        }
    }

    fun ByteBuf.writeSmart(value: Int) {
        if (value >= 0x80) {
            writeShort(value + 0x8000)
        } else {
            writeByte(value)
        }
    }

    fun ByteBuf.writeString(string: String) {
        val array = string.toCharArray()
        for (ch in array) {
            writeByte(ch.toInt())
        }
        writeByte(STRING_TERMINATOR)
    }

    fun ByteBuf.writeBytes(buf: ByteBuf, mod: ByteModification) {
        for (i in 0 until buf.readableBytes()) {
            writeByte(buf.readByte().toInt(), mod)
        }
    }

    fun ByteBuf.writeBytes(data: ByteArray, length: Int = data.size) {
        for (i in 0 until length - 1) {
            writeByte(data[i].toInt())
        }
    }

    fun ByteBuf.writeBytesAdd(data: ByteArray, length: Int = data.size) {
        for (i in 0 until length) {
            writeByte(data[i].toInt(), ByteModification.ADD)
        }
    }

    fun ByteBuf.writeBytesReverse(data: ByteArray, length: Int = data.size) {
        for (i in length - 1 downTo 0) {
            writeByte(data[i].toInt())
        }
    }
}