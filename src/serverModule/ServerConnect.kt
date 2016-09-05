package serverModule

//import android.util.Log
//import server.serverModule.Response
import org.jetbrains.annotations.Nullable
import java.io.DataInputStream
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.Socket
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created by kr3v on 03.09.2016.
 * TODO:
 */
class ServerConnect {

    var connectListener = object : ConnectListener {
        override fun onError() {

        }

        override fun onDone() {

        }
    }

    fun getSHA512(): MessageDigest {
        val temp = MessageDigest.getInstance("SHA-512")
        temp.reset()
        return temp
    }

    fun login(username: String, password: String) {
        Thread(Runnable {
            val socket = Socket("192.168.0.101", 8080)
            var response = ""
            socket.use {
                val inputStream = DataInputStream(socket.inputStream)
                val outputStream = DataOutputStream(socket.outputStream)
                outputStream.writeUTF("login")
                outputStream.writeUTF(username)
                val salt = inputStream.readUTF()
                outputStream.writeUTF(saltedHash(password, salt))
                response = inputStream.readUTF()
            }
            if (response.contains("OK", true)) {
                connectListener.onDone()
            } else {
                connectListener.onError()
            }
        }).start()

    }

    fun register(username: String, password: String) {
        Thread(Runnable {
            val socket = Socket("192.168.0.101", 8080)
            var response = ""
            socket.use {
                val inputStream = DataInputStream(socket.inputStream)
                val outputStream = DataOutputStream(socket.outputStream)

                outputStream.writeUTF("register")
                val salt = inputStream.readUTF()
                val toSend = "$username#${saltedHash(password, salt)}"
                outputStream.writeUTF(toSend)
                response = inputStream.readUTF()
            }
            if (response.contains("OK", true)) {
                connectListener.onDone()
            } else {
                connectListener.onError()
            }
        }).start()
    }

    fun vkLogin(response: String) {
        Thread(Runnable {
            val socket = Socket("192.168.0.101", 8080)
            socket.use {
                val inputStream = DataInputStream(socket.inputStream)
                val outputStream = DataOutputStream(socket.outputStream)
            }
            connectListener.onDone()
        }).start()
    }

    private val sha512 = getSHA512()

    private fun bin2hex(data: ByteArray) = String.format("%0" + data.size * 2 + "X", BigInteger(1, data))

    private fun hash(string: String) = bin2hex(sha512.digest(string.toByteArray(Charset.forName("US-ASCII"))))

    private fun saltedHash(string: String, salt: String) = hash(salt + hash(salt))
}