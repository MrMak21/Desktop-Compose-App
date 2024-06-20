package extensions

import java.io.File

fun File?.onError(action: () -> Unit): File? {
    return if (this == null) {
        action()
        null
    } else {
        this
    }
}

fun File?.onSuccess(action: (File) -> Unit): File? {
    return if (this == null) {
        null
    } else {
        action(this)
        this
    }
}