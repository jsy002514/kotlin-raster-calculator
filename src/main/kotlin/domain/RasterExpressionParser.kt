package domain

import java.util.Stack

class RasterExpressionParser {
    private val precedence = mapOf(
        "+" to 1, "-" to 1,
        "*" to 2, "/" to 2
    )

    fun parseToPostfix(expression: String): List<String> {
        val output = mutableListOf<String>()
        val operatorStack = Stack<String>()
        val tokens = expression.split(" ")

        for (token in tokens) {
            processToken(token, output, operatorStack)
        }

        popAllOperators(operatorStack, output)
        return output
    }

    private fun processToken(
        token: String,
        output: MutableList<String>,
        operatorStack: Stack<String>
    ) {
        if (token == "(") {
            operatorStack.push(token)
            return
        }
        if (token == ")") {
            popOperatorsUntilParenthesis(operatorStack, output)
            return
        }
        if (token in precedence) {
            popOperatorsByPrecedence(token, operatorStack, output)
            operatorStack.push(token)
            return
        }
        output.add(token)
    }

    private fun popOperatorsUntilParenthesis(
        stack: Stack<String>,
        output: MutableList<String>
    ) {
        while (stack.isNotEmpty() && stack.peek() != "(") {
            output.add(stack.pop())
        }
        stack.pop()
    }

    private fun popOperatorsByPrecedence(
        currentToken: String,
        stack: Stack<String>,
        output: MutableList<String>
    ) {
        val currentPrecedence = precedence[currentToken] ?: 0

        while (stack.isNotEmpty() &&
            stack.peek() != "(" &&
            ((precedence[stack.peek()] ?: 0) >= currentPrecedence)
        ) {
            output.add(stack.pop())
        }
    }

    private fun popAllOperators(
        stack: Stack<String>,
        output: MutableList<String>
    ) {
        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }
    }
}
