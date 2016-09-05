package serverModule.listeners

import serverModule.Response

/**
 * Created by kr3v on 04.09.2016.
 */
interface ConnectListener {
    fun onDone()

    fun onError()

    fun fromResponse(string: String) {
        when (Response.valueOf(string)) {
           Response.OK -> onDone()
           Response.ERROR, Response.NOSUCHMETHOD -> onError()
        }
    }
}