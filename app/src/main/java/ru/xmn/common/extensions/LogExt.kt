package ru.xmn.common.extensions

import android.util.Log

inline fun <reified T> T.log(logText: String) {
    val simpleName = formatLogTag(T::class.java.canonicalName)
    Log.d(simpleName, logText)
}

fun formatLogTag(canonicalName: String): String {
    return if (canonicalName.matches(""".*\.Companion""".toRegex())) {
        """[^.]+.Companion""".toRegex().find(canonicalName)?.value?:""
    } else
        """[^.]*${'$'}""".toRegex().find(canonicalName)?.value?:""
}