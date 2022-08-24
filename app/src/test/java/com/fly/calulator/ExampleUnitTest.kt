package com.fly.calulator

import com.fly.calulator.logic.util.CalculatorUtil
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println( CalculatorUtil.evalInput("1+1+5-2×3÷2"))
        assertEquals(4, 2 + 2)
    }
}