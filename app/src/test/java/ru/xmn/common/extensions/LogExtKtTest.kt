package ru.xmn.common.extensions

import org.junit.Assert.*
import org.junit.Test

/**
 * Created by USER on 17.10.2017.
 */
class LogExtKtTest {
    @Test
    internal fun log() {
        assertEquals("LogClass.Companion", LogClass.printLog())
        assertEquals("LogClass", LogClass().printLog())
    }
}

class LogClass {

    fun printLog(): String {
        return formatLogTag(LogClass::class.java.canonicalName)
    }

    companion object {
        fun printLog(): String {
            return formatLogTag(LogClass.Companion::class.java.canonicalName)
        }
    }
}