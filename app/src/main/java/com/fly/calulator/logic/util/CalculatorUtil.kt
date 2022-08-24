package com.fly.calulator.logic.util

import android.util.Log


object CalculatorUtil {
    fun handleInput(nowContent:List<String>, newClick:String):CalculatorResponse{
        if (nowContent.size>20 && newClick!="C" && newClick!="B"){ return CalculatorResponse(status = InputStatus.LENGTH_LIMIT, result = nowContent)}
        val newContent = nowContent.toMutableList()
        val calculatorChar = listOf("+", "-", "×", "÷")
        val numberChar = listOf("0","1","2","3","4","5","6", "7", "8","9","00")
        when(newClick){
            "B" -> { newContent.removeLast() }
            "C" -> { newContent.clear() }
            "E" -> {
                newContent.clear()
                Log.d("nowContentValue-Util", nowContent.toString())
                if (nowContent.isEmpty()){
                    newContent.clear()
                }else {
                    val res = evalInput(nowContent.joinToString("").replace("%", "÷100"))
                    newContent.addAll(res.toString().split(""))
                }
            }
            "." -> {
                val dotLastIndex = newContent.indexOfLast{it=="."}
                if (dotLastIndex==-1 && (newContent.isEmpty() || newContent.last() in numberChar || newContent.last() in calculatorChar) ){
                    newContent.add(newClick)
                }else if (
                    containOrNot(
                        nowContent.slice(dotLastIndex until nowContent.size),
                        calculatorChar
                    ) && (newContent.last() in numberChar || newContent.last() in calculatorChar)
                ){
                    newContent.add(newClick)
                }
            }
            in calculatorChar -> {
                if (newContent.isNotEmpty()) {
                    if (newContent.last() in calculatorChar) {
                        newContent.removeLast()
                        newContent.add(newClick)
                    }else{
                        newContent.add(newClick)
                    }
                }
            }
            in numberChar -> {
                newContent.add(newClick)
            }
            else -> { newContent.add(newClick) }
        }
        return CalculatorResponse(status = InputStatus.HANDLE_SUCCESS, result = newContent.toList())
    }

    fun evalInput(input:String):Float{
        when {
            "+" in input -> {
                val inputSplit = input.split("+")
                var res = 0f
                inputSplit.forEach {
                    res += evalInput(it)
                }
                return res
            }
            "-" in input -> {
                val inputSplit = input.split("-")
                var res = 0f
                inputSplit.forEachIndexed { index, item ->
                    if (index==0){
                        res += evalInput(item)
                    }else{
                        res -= evalInput(item)
                    }
                }
                return res
            }
            "×" in input -> {
                val inputSplit = input.split("×")
                var res = 1f
                inputSplit.forEach {
                    res *= evalInput(it)
                }
                return res
            }
            "÷" in input -> {
                val inputSplit = input.split("÷")
                var res = 0f
                inputSplit.forEachIndexed { index, item ->
                    if (index==0){
                        res = evalInput(item)
                    }else{
                        res /= evalInput(item)
                    }
                }
                return res
            }
        }
        return input.toFloat()
    }

    fun containOrNot(list1:List<String>, list2:List<String>):Boolean{
        val intersection = list1.filter { list2.contains(it) }
        return intersection.isNotEmpty()
    }
}

data class CalculatorResponse(
    val status: InputStatus,
    val result: List<String> = listOf()
)


enum class InputStatus{
    HANDLE_SUCCESS, LENGTH_LIMIT, EQUAL
}



