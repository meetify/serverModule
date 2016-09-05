@file:Suppress("unused")

package serverModule

import serverModule.listeners.ConnectListener
import serverModule.listeners.LoginListener
import java.math.BigInteger
import java.net.Socket
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created by kr3v on 03.09.2016.
 * This object is used to allow client doing it's tasks without interfacing with server
 */
class ServerConnect {

    constructor() {
        Thread(socket).start()
    }

    var connectListener = object : ConnectListener {
        override fun onError() {

        }

        override fun onDone() {

        }
    }
    var loginListener = object : LoginListener {
        override fun onLogin() {

        }

        override fun onLoginError() {

        }
    }

    companion object {
        fun init() : CustomSocket {
            val ret = CustomSocket(Socket(ipAddress, port))
            ret.socket.close()
            return ret
        }

        val ipAddress = "192.168.0.101"
        val port = 8080
        val socket = init()
    }

    fun login(username: String, password: String) {
        Thread(Runnable {
            socket.writeUTF("login")
            socket.writeUTF(username)
            val salt = socket.readUTF()
            socket.writeUTF(saltedHash(password, salt))
            connectListener.fromResponse(socket.readUTF())
        }).start()

    }

    fun register(username: String, password: String) {
        Thread(Runnable {
            socket.writeUTF("register")
            val salt = socket.readUTF()
            val toSend = "$username#${saltedHash(password, salt)}"
            socket.writeUTF(toSend)
            connectListener.fromResponse(socket.readUTF())
        }).start()
    }

    fun vkLogin() {
        Thread(Runnable {
            socket.writeUTF("vklogin")
            if (!isOK(socket.readUTF())) {
                loginListener.onLoginError()
                return@Runnable
            }
            connectListener.fromResponse(socket.readUTF())
        }).start()
    }

    private fun isOK(string: String): Boolean {
        return Response.valueOf(string) == Response.OK
    }

    private fun getSHA512(): MessageDigest {
        val temp = MessageDigest.getInstance("SHA-512")
        temp.reset()
        return temp
    }

    private val sha512 = getSHA512()

    private fun bin2hex(data: ByteArray) = String.format("%0" + data.size * 2 + "X", BigInteger(1, data))

    private fun hash(string: String) = bin2hex(sha512.digest(string.toByteArray(Charset.forName("US-ASCII"))))

    private fun saltedHash(pass: String, salt: String) = hash(salt + hash(pass))
}