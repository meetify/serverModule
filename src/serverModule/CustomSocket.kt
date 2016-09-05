package serverModule

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket

/**
 * Created by kr3v on 05.09.2016.
 * Custom sockets are used to easily work with sockets (their I/O) and implement autoclose due to non-using socket
 */

@Suppress("unused")
open class CustomSocket (var socket: Socket,
                        var input: DataInputStream = DataInputStream(socket.inputStream),
                        var output: DataOutputStream = DataOutputStream(socket.outputStream),
                        val inetAddress: InetAddress = socket.inetAddress,
                        val port: Int = socket.port) : Runnable {

    private val sleepLength: Long = 15 * 1000
    private var update = false

    override fun run() {
        do {
            if (!socket.isClosed) {
                update = false
                Thread.sleep(sleepLength)
            }
        } while (update)
        socket.close()
    }

    open fun read(fn: () -> Any): Any {
        update()
        try {
            return fn()
        } finally {
            update = true
        }
    }

    open fun write(fn: (elem: Any) -> Unit, any: Any) {
        update()
        update = true
        fn(any)
    }

    open fun update() {
        if (socket.isClosed) {
            socket = Socket(inetAddress, port)
            input  = DataInputStream(socket.inputStream)
            output = DataOutputStream(socket.outputStream)
        }
    }

    fun readUTF() = read { input.readUTF() } as String

    fun readInt() = read { input.readInt() } as Int

    fun writeUTF(string: String) = write({ output.writeUTF(string) }, string)

    fun writeInt(int: Int) = write({ output.writeInt(int) }, int)

    fun available() = input.available() > 0
}