package com.rnd.customunitview

import java.util.regex.Pattern

fun main() {

    /**
     * Inputs
     * 1234
     * 12344.343322
     * 12344.34
     * 00
     * 0
     * 1223
     */

    val decimalPoint = 1
    var acceptDecimal = ""

    for (i in 1..decimalPoint) {
        acceptDecimal = acceptDecimal.plus("\\d")
    }

    val list = arrayListOf("1234", "1344.343322", "12344.34", "00", "0", "541.28986289334444628736287")

    val regex = "\\d+(\\.$acceptDecimal?)?"
    val pattern = Pattern.compile(regex)

    for (input in list) {

        val startTime = System.nanoTime()

        val matcher = pattern.matcher(input)

        if (matcher.find()) {
            val result = matcher.group(0)
            val floatValue = result?.toFloat()
            print(floatValue)
        } else {
            print("No matched found")
        }

        val endTime = System.nanoTime()

        val timeElapsed = (endTime - startTime).toFloat()

        println("   Execution time in milliseconds  : ${timeElapsed/1000000}")
    }
}
