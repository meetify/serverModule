package serverModule.data

import java.util.*

/**
 * Created by kr3v on 09.09.2016.
 */
class Name {
    val names = HashMap<String, String>()

    fun get(key: String = ""): String? {
        return names[key]
    }
}