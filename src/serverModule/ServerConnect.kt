package serverModule

//import android.util.Log
//import server.serverModule.Response
import java.io.DataInputStream
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.Socket
import java.nio.charset.Charset
import java.security.MessageDigest

/**
 * Created by kr3v on 03.09.2016.
 */
object ServerConnect {

    var connectListener = object : ConnectListener {
        override fun onDone() {
//            return serverModule.Response.ERROR
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    val sha512 = {
        val temp = MessageDigest.getInstance("SHA-512")
        temp.reset()
        temp
    } as MessageDigest

    fun login(username: String, password: String) {
        val socket = Socket("192.168.0.101", 8080)
        socket.use {
            val inputStream = DataInputStream(socket.inputStream)
            val outputStream = DataOutputStream(socket.outputStream)
            outputStream.writeUTF("login")
            outputStream.writeUTF(username)
            val salt = inputStream.readUTF()
            outputStream.writeUTF(saltedHash(password, salt))
//            Log.d(Log.DEBUG.toString(), inputStream.readUTF())
        }
        connectListener.onDone()
    }

    fun register(username: String, password: String) {
        val socket = Socket("192.168.0.101", 8080)
        socket.use {
            val inputStream = DataInputStream(socket.inputStream)
            val outputStream = DataOutputStream(socket.outputStream)

            outputStream.writeUTF("register")
            val salt = inputStream.readUTF()
            val toSend = "$username#${saltedHash(password, salt)}"
//            Log.d(Log.DEBUG.toString(), toSend)
            outputStream.writeUTF(toSend)
//            Log.d(Log.DEBUG.toString(), inputStream.readUTF())
        }
    }

    fun bin2hex(data: ByteArray) = String.format("%0" + data.size * 2 + "X", BigInteger(1, data))

    fun hash(string: String) = bin2hex(sha512.digest(string.toByteArray(Charset.forName("US-ASCII"))))

    fun saltedHash(string: String, salt: String) = hash(salt + hash(salt))
}