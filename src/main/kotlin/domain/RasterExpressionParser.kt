package domain

import java.util.Stack

class RasterExpressionParser {
    private val precedence = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2
    )

    fun parseToPostfix(expression: String): List<String>{
        val output = mutableListOf<String>()
        val operatorStack = Stack<String>()

        val tokens = expression.split(" ")

        for(token in tokens){
            when{
                token == "(" -> operatorStack.push(token)

                token == ")" -> {
                    while (operatorStack.isNotEmpty() && operatorStack.peek() != "("){
                        output.add(operatorStack.pop())
                    }
                    operatorStack.pop()
                }

                token in precedence -> {
                    while (operatorStack.isNotEmpty() &&
                        operatorStack.peek() != "(" &&
                        (precedence[operatorStack.peek()] ?: 0) >= (precedence[token] ?: 0)
                    ){
                        output.add(operatorStack.pop())
                    }
                    operatorStack.push(token)
                }

                else -> output.add(token)
            }
        }

        while(operatorStack.isNotEmpty()){
            output.add(operatorStack.pop())
        }

        return output
    }
}
