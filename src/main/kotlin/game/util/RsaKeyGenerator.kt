package game.util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

fun main() {

    try {
        val factory = KeyFactory.getInstance("RSA")
        val keyGen = KeyPairGenerator.getInstance("RSA")

        keyGen.initialize(1024);

        val keyPair = keyGen.genKeyPair()
        val privateKey = keyPair.private
        val publicKey = keyPair.public

        val privateSpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)
        RSAKeyGenerator.writeKeyKotlin(File("./resources/rsa/server.rsa"), privateSpec.modulus, privateSpec.privateExponent)

        val publicSpec = factory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)
        RSAKeyGenerator.writeKeyJava(File("./resources/rsa/client.rsa"), publicSpec.modulus, publicSpec.publicExponent)
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

object RSAKeyGenerator{
    fun writeKeyJava(file: File, modulus: BigInteger, exponent: BigInteger) {
        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.write("rsaKeyExponent = new BigInteger(\"$exponent\");\n")
            writer.write("rsaKeyModulus = new BigInteger(\"$modulus\");")
            writer.flush()
            writer.close()
            println("Completed generating client keys")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun writeKeyKotlin(file: File, modulus: BigInteger, exponent: BigInteger) {
        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.write("private val RSA_EXPONENT = new BigInteger(\"$exponent\");\n")
            writer.write("private val RSA_MODULUS = new BigInteger(\"$modulus\");")
            writer.flush()
            writer.close()
            println("Completed generating server keys")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

