package ru.xmn.common.extensions

import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.junit.Test

import org.junit.Assert.*

/**
 * Created by USER on 11.10.2017.
 */
class DateExtKtTest {
    @Test
    fun stampInSeconds() {
        println(alg(emptyList(), listOf(20, 10, 40, 80, 55, 39, 30, 60)))
    }

    fun alg(left: List<Int>, right: List<Int>): List<List<Int>> {
        if (right.isEmpty())
            return listOf(left)

        val result: MutableList<List<Int>> = if (!left.isEmpty() && listSumEqual100(left)) mutableListOf(left) else mutableListOf()
        val rightSize = right.size
        for (x in 0 until rightSize) {
            val list = alg(left + right[x], right.subList(x + 1, rightSize))
            list.filter { listSumEqual100(it) }.forEach { result += it }
        }

        return result
    }

    private fun listSumEqual100(left: List<Int>) =
            left.foldRight(0, { i, acc -> acc + i }) == 100
}

